# AI Workout Planner Logic

## Purpose

This document defines the system logic for an AI-powered workout planner where:

- the **frontend chat** is the user interface
- the **backend** handles the AI logic and all third-party API calls
- workout plans are stored in the **database**
- a user can have **one or many plans**
- one plan represents a **weekly repeating structure**
- the user can later **view, adjust, and activate** a plan as their main plan

This document is intended to guide implementation in Cursor.

---

## Core Product Flow

### User experience

1. The user opens the chat screen.
2. The user talks naturally with the AI.
3. The AI asks follow-up questions until it has enough data to build a workout plan.
4. When enough information is collected, the frontend shows a loading state.
5. The backend generates a weekly training plan using:
   - collected user profile data
   - internal planning rules
   - the exercise API
   - the AI model
6. The backend stores the generated plan in the database for that user.
7. The chat returns a **plan card** in the chat UI.
8. The user can click the plan card to open the full plan.
9. The user can adjust the plan.
10. The user can click a button like **"Use as Main Plan"** to make it the active plan.

---

## Architecture Rules

### Security rules

- The **frontend must never call the exercise API directly**.
- The **frontend must never call the AI provider directly**.
- All API keys must stay in the backend as environment variables.
- The backend is the single place that:
  - collects and validates planning data
  - calls the exercise API
  - calls the AI model
  - stores plan data in the database

---

## Main Logic Principle

The AI should **not invent the full plan from scratch without constraints**.

The correct flow is:

1. collect user data
2. normalize it into structured fields
3. determine plan rules in the backend
4. fetch exercise candidates from the exercise API
5. filter those candidates
6. send only relevant structured data to the AI
7. let the AI assemble the final weekly plan
8. store the necessary plan data in the database

In other words:

- **backend rules decide the structure**
- **exercise API provides valid exercise options**
- **AI assembles and explains the plan**

---

## Required User Data To Collect In Chat

The AI chat should collect enough information to generate a realistic plan.

### Required fields

- goal
  - fat loss
  - muscle gain
  - strength
  - general fitness
  - mobility
- age
- sex or gender if your app needs it
- height
- weight
- experience level
  - beginner
  - intermediate
  - advanced
- training days per week
- available time per session
- available equipment
- training location
  - gym
  - home
  - mixed
- work schedule / weekly availability
- injuries, pain, or limitations
- preferred split if any
- disliked exercises if any

### Optional but useful

- activity level
- sleep quality
- recovery quality
- cardio preference
- favorite body parts to focus on

---

## Chat Collection Logic

The frontend chat sends user messages to the backend.

The backend should maintain a structured **planning session state**.

### Example planning session state

```json
{
  "userId": "uuid",
  "status": "collecting",
  "collectedFields": {
    "goal": "muscle_gain",
    "experienceLevel": "beginner",
    "daysPerWeek": 4,
    "minutesPerSession": 45,
    "equipment": ["dumbbell", "bench"],
    "location": "home"
  },
  "missingFields": ["heightCm", "weightKg", "injuries", "weeklyAvailability"]
}
```

### Chat behavior

- If required fields are missing, the AI should ask the next best question.
- The backend should extract structured values from the conversation.
- The backend should not create a plan until the minimum required data is present.
- Once enough data is available, the backend moves the session to a **ready_to_generate** state.

---

## Minimum Data Required Before Plan Generation

At minimum, require:

- goal
- experience level
- training days per week
- minutes per session
- equipment or location
- injuries/limitations
- height
- weight

You may also require:
- age
- weekly availability

---

## Backend Rule Engine

Before the AI creates the plan, the backend must derive planning rules.

### What the backend should decide

- split type
- target number of workout days
- number of exercises per day
- volume level
- allowed difficulty level
- rest day placement strategy
- equipment restrictions
- injury restrictions
- exercise categories per day

### Example rules

#### Frequency-based split rules

- **2 to 3 days/week**
  - default to full body
- **4 days/week**
  - default to upper/lower
- **5 to 6 days/week**
  - default to push/pull/legs or another advanced split

#### Session duration rules

- **20 to 30 min**
  - 4 to 5 exercises max
  - optional supersets
- **45 to 60 min**
  - 5 to 7 exercises
- **60+ min**
  - full standard structure

#### Goal rules

- **fat loss**
  - more compound movements
  - moderate volume
  - optional cardio finisher
- **muscle gain**
  - moderate to high volume
  - hypertrophy-focused rep ranges
- **strength**
  - lower rep ranges on main lifts
  - longer rest
  - fewer movements
- **general fitness**
  - balanced full-body structure
  - moderate intensity

#### Experience rules

- **beginner**
  - simple exercises
  - low complexity
  - lower volume
  - avoid too many technical movements
- **intermediate**
  - standard volume
- **advanced**
  - greater exercise variety
  - more volume if recovery supports it

#### Injury and limitation rules

Examples:
- shoulder pain -> avoid or reduce overhead pressing if needed
- lower back pain -> avoid high-fatigue unsupported hinges if needed
- knee pain -> prefer more knee-tolerant lower-body options

These are adaptation rules, not medical advice.

---

## Exercise Fetching Strategy

The backend should use the exercise API to build an exercise pool.

### Important rule

The AI should receive a **filtered shortlist**, not the full database.

### Exercise pool creation process

1. Determine required training categories for the selected split.
2. Fetch candidate exercises from the exercise API.
3. Filter by equipment.
4. Filter by body part or muscle.
5. Filter by user limitations.
6. Remove duplicates or very similar movements.
7. Group exercises by role.

### Useful role groups

- horizontal push
- vertical push
- horizontal pull
- vertical pull
- squat pattern
- hinge pattern
- lunge / unilateral leg
- glute-focused
- calf
- biceps
- triceps
- shoulders
- core

---

## AI Generation Logic

After the backend has:
- the structured user profile
- the derived planning rules
- the filtered exercise list

the backend calls the AI to generate the final plan.

### What the AI should do

The AI should:

- choose exercises only from the supplied allowed exercise pool
- build a weekly repeating plan
- assign training days and rest days
- assign exercise order
- assign sets, reps, rest times, and notes
- keep the plan realistic for the user's available time
- produce a consistent output format

### What the AI should not do

The AI should not:

- invent exercises not in the approved pool unless the backend explicitly allows it
- ignore time limits
- ignore equipment restrictions
- ignore injury restrictions
- create a different random week every time if the plan is meant to repeat weekly

---

## Weekly Repeating Plan Model

Each created plan is a **weekly template**.

Example:
- Day 1: Pull
- Day 2: Push
- Day 3: Rest
- Day 4: Legs
- Day 5: Upper
- Day 6: Rest
- Day 7: Rest

This weekly structure repeats every week until:
- the user edits it
- the user switches to another plan
- the system generates a replacement plan

The user should be able to see the same weekly plan each week unless changed.

---

## Plan Storage Strategy

### Important rule

Store **only the information needed by the product**.

Do **not** store all raw AI prompt data, all API response payloads, or unnecessary duplicated metadata.

### Store only necessary plan data such as

#### Plan-level
- plan id
- user id
- plan name
- goal
- split type
- days per week
- session duration target
- active/inactive status
- created at
- updated at

#### Weekly structure
- day number
- day name if needed
- workout type
- is rest day

#### Exercise assignment
- day number
- exercise name
- optional source exercise id from third-party API
- target muscle/body part if needed
- sets
- reps or rep range
- rest seconds
- exercise order
- notes

#### Adjustment metadata
- version number
- manually edited flag
- active plan flag

### Avoid storing unnecessary data such as

- full exercise API responses
- unused exercise candidates
- full conversational history inside the plan table
- hidden reasoning from the AI
- duplicated user profile data that already exists in the user profile table

---

## Suggested Database Model

This is a logical model, not a mandatory final schema.

### users
- id
- name
- email
- profile fields as needed

### ai_planning_sessions
Used while collecting information in chat.

- id
- user_id
- status
- collected_fields_json
- missing_fields_json
- created_at
- updated_at

### plans
A user can have one or many plans.

- id
- user_id
- name
- goal
- split_type
- days_per_week
- session_duration_minutes
- is_active
- version
- created_at
- updated_at

### plan_days
Defines the weekly repeating structure.

- id
- plan_id
- day_number
- title
- workout_type
- is_rest_day
- notes

### plan_day_exercises
Stores exercise assignments for each day.

- id
- plan_day_id
- source_exercise_id nullable
- exercise_name
- body_part nullable
- muscle nullable
- equipment nullable
- sets
- reps_text
- rest_seconds nullable
- order_index
- notes nullable

### chat_messages
If you need to render the conversation history.

- id
- user_id
- session_id
- role
- message_type
- content
- metadata_json
- created_at

Possible message_type values:
- text
- loading
- plan_card
- system

---

## Plan Activation Logic

A user can have many plans.
Only one should usually be the **main active plan**.

### Behavior

- when a new plan is created, it can default to inactive unless product requirements say otherwise
- when the user clicks **Use as Main Plan**
  - set selected plan `is_active = true`
  - set all other user plans `is_active = false`

This should happen transactionally.

---

## Plan Editing Logic

The user should be able to open a created plan and adjust it.

### Adjustments may include

- swap an exercise
- change sets or reps
- change rest day placement
- rename a day
- replace a day split
- change plan title

### Recommended approach

- keep the original stored plan version
- create a new version or update with clear audit rules
- mark `manually_edited = true` if needed
- keep active plan selection independent from draft edits until saved

---

## Frontend Chat UI Logic

### Chat states

The chat should support these message states:

- user message
- AI text response
- AI follow-up question
- loading indicator while plan is being created
- plan card response after creation

### Plan creation flow in chat

1. User sends message.
2. Backend checks planning session status.
3. If information is missing, backend returns AI follow-up question.
4. If enough information is present:
   - frontend shows loading message/card/spinner
   - backend generates plan
   - backend stores plan
   - backend returns a plan-card payload
5. Frontend replaces or follows the loading state with a clickable plan card.

---

## Plan Card Requirements

The plan card shown in chat should include enough summary information for the user to understand what was created.

### Suggested content

- plan name
- goal
- split type
- days per week
- session duration
- creation date
- status badge
  - draft
  - active
- CTA button or click action to open full plan

---

## API Design Suggestions

These are example backend route ideas.

### Chat routes

- `POST /chat/message`
  - receives user message
  - updates planning session
  - returns either:
    - next AI question
    - loading state instruction
    - generated plan card

### Planning routes

- `POST /plans/generate`
  - internal or protected route
  - generates a plan for a user from collected data

### Plan routes

- `GET /plans`
  - list all plans for a user

- `GET /plans/:planId`
  - get full plan details

- `PATCH /plans/:planId`
  - update editable plan data

- `POST /plans/:planId/activate`
  - set plan as main plan

- `POST /plans/:planId/duplicate`
  - optional

---

## Generation Pipeline

### Recommended backend flow

1. Receive chat message.
2. Save message.
3. Extract structured data from user input.
4. Update planning session.
5. Check if minimum data is collected.
6. If not enough:
   - ask next question
7. If enough:
   - derive planning rules
   - fetch exercise candidates from third-party API
   - filter candidates
   - call AI with strict structured prompt
   - validate AI response
   - persist only needed plan data
   - create chat message with plan card payload
   - return response to frontend

---

## Validation Requirements

### Validate user data
- numeric fields
- units
- required fields
- enum values

### Validate AI output
- valid JSON or schema
- day count matches requested weekly structure
- no exercise exceeds time constraints unrealistically
- exercise entries contain required fields
- only approved or allowed exercises are used

### Validate plan persistence
- plan belongs to the user
- one active plan max per user
- no orphan plan day rows
- exercise order indexes are valid

---

## Recommended AI Output Shape

Use a strict schema from the AI.

Example concept:

```json
{
  "planName": "4-Day Upper Lower Plan",
  "goal": "muscle_gain",
  "splitType": "upper_lower",
  "daysPerWeek": 4,
  "sessionDurationMinutes": 45,
  "week": [
    {
      "dayNumber": 1,
      "title": "Upper A",
      "workoutType": "upper",
      "isRestDay": false,
      "exercises": [
        {
          "exerciseName": "Dumbbell Bench Press",
          "sourceExerciseId": "123",
          "sets": 3,
          "repsText": "8-12",
          "restSeconds": 90,
          "notes": "Controlled tempo"
        }
      ]
    }
  ]
}
```

---

## Persistence Principle

The system should store the plan as a **weekly template**, not as thousands of calendar instances.

Do not generate one database record for every future calendar week.
Instead:
- store the weekly template once
- render it as repeating in the UI
- if needed later, map dates to day numbers dynamically

---

## Important Non-Functional Requirements

### Performance
- keep third-party API calls limited and purposeful
- do not fetch the entire exercise database if not needed
- cache metadata like body parts, muscles, and equipment where useful

### Reliability
- handle AI failure gracefully
- handle third-party API timeouts gracefully
- store partial session progress
- retry safe backend operations when appropriate

### Maintainability
- separate chat orchestration, plan rules, exercise service, AI service, and persistence layers
- keep prompt-building logic isolated
- use DTOs / schemas / validators

### Observability
- log plan generation steps
- log third-party API failures
- log AI validation failures
- include user-safe error messages

---

## Recommended Service Separation

Suggested backend modules:

- `chat-orchestrator`
- `planning-session-service`
- `user-profile-normalizer`
- `plan-rule-engine`
- `exercise-api-service`
- `exercise-filter-service`
- `ai-plan-generator`
- `plan-validator`
- `plan-repository`
- `chat-message-repository`

---

## Final Build Goal

Build a system where:

- the user chats naturally in the frontend
- the backend safely manages data collection
- the backend uses the exercise API and AI to generate a weekly repeating workout plan
- the plan is stored for the user
- the user can view it from a card in chat
- the user can edit it
- the user can activate it as the main plan
- only the necessary plan data is stored in the database

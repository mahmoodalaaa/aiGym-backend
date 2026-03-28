# Exercise API Reference For Backend Integration

## Purpose

This document describes the third-party exercise API endpoints that should be used by the backend workout planner.

Important rules:
- These APIs are **third-party APIs**, not internal system APIs.
- They must be called **only from the backend**.
- Their API key must be stored securely in environment variables.
- The frontend must never call them directly.

Also important:
- When using endpoints with pagination, assume the **maximum limit is 25**.
- Always explicitly send `offset` and `limit`.
- If more items are needed, paginate with a higher offset.
- Do not assume that limit values above 25 will work.

---

## Base Host

Host header used in the examples:

```text
exercisedb-13001.p.rapidapi.com
```

Base path pattern used in the examples:

```text
https://exercisedb-13001.p.rapidapi.com/api/exercises1/
```

---

## Authentication And Headers

Required headers shown in the examples:

```text
Content-Type: application/json
x-rapidapi-host: exercisedb-13001.p.rapidapi.com
x-rapidapi-key: <YOUR_API_KEY>
```

### Security requirement

Do not hardcode the RapidAPI key in frontend code.
Store it in backend environment variables, for example:

- `RAPIDAPI_KEY`
- `RAPIDAPI_HOST`

The backend should inject the headers when making requests.

---

## Pagination Rule

For endpoints that support pagination:

- always send `offset`
- always send `limit`
- **maximum limit is 25**

### Recommended usage

- first page:
  - `offset=0&limit=25`
- second page:
  - `offset=25&limit=25`
- third page:
  - `offset=50&limit=25`

This must be documented in the backend integration so the implementer does not accidentally request more than 25 items in a single call.

---

## Endpoint 1: SearchExercises

### Purpose

Search exercises by keyword.

Use this when:
- you need keyword-based lookup
- you want a fallback search
- you want to find exercises by text like `biceps`, `squat`, or `press`

### Example request

```bash
curl --request POST \
  --url 'https://exercisedb-13001.p.rapidapi.com/api/exercises1/SearchExercises?keyword=biceps&offset=0&limit=10' \
  --header 'Content-Type: application/json' \
  --header 'x-rapidapi-host: exercisedb-13001.p.rapidapi.com' \
  --header 'x-rapidapi-key: <YOUR_API_KEY>' \
  --data '{"key1":"value","key2":"value"}'
```

### Query parameters

- `keyword`
  - the text to search for
- `offset`
  - pagination starting index
- `limit`
  - number of items to fetch
  - **maximum allowed limit: 25**

### Backend usage guidance

Use this endpoint:
- as a fallback search
- to find specific exercise names
- to support exercise replacement features
- to search for alternatives when a user wants to swap an exercise

---

## Endpoint 2: GetExercisesByMuscle

### Purpose

Fetch exercises for a specific muscle.

Use this when:
- the rule engine knows a workout day needs a specific muscle emphasis
- you want targeted exercise candidates for plan generation

### Example request

```bash
curl --request POST \
  --url 'https://exercisedb-13001.p.rapidapi.com/api/exercises1/GetExercisesByMuscle?muscle=biceps&offset=0&limit=10' \
  --header 'Content-Type: application/json' \
  --header 'x-rapidapi-host: exercisedb-13001.p.rapidapi.com' \
  --header 'x-rapidapi-key: <YOUR_API_KEY>' \
  --data '{"key1":"value","key2":"value"}'
```

### Query parameters

- `muscle`
  - target muscle name, for example `biceps`
- `offset`
  - pagination starting index
- `limit`
  - number of items to fetch
  - **maximum allowed limit: 25**

### Backend usage guidance

Use this endpoint when building a focused exercise pool for:
- biceps
- triceps
- chest
- quads
- hamstrings
- shoulders
- back muscles
- other targeted muscle groups supported by the API

---

## Endpoint 3: GetExercisesByBodyparts

### Purpose

Fetch exercises by body part.

Use this when:
- you want broader grouping than a single muscle
- you want to fetch exercises for an area like lower legs or upper legs

### Example request

```bash
curl --request POST \
  --url 'https://exercisedb-13001.p.rapidapi.com/api/exercises1/GetExercisesByBodyparts?bodypart=lower%20legs&limit=10&offset=0' \
  --header 'Content-Type: application/json' \
  --header 'x-rapidapi-host: exercisedb-13001.p.rapidapi.com' \
  --header 'x-rapidapi-key: <YOUR_API_KEY>' \
  --data '{"key1":"value","key2":"value"}'
```

### Query parameters

- `bodypart`
  - body part name, for example `lower legs`
- `offset`
  - pagination starting index
- `limit`
  - number of items to fetch
  - **maximum allowed limit: 25**

### Backend usage guidance

Use this endpoint when:
- the rule engine works at body-part level
- a workout day is grouped more broadly
- you need alternatives for a body region rather than a single muscle

---

## Endpoint 4: GetExercisesByEquipment

### Purpose

Fetch exercises by equipment.

Use this when:
- equipment availability is the primary constraint
- the user trains at home
- the user only has limited equipment
- you want to build the first filtered exercise pool

### Example request

```bash
curl --request POST \
  --url 'https://exercisedb-13001.p.rapidapi.com/api/exercises1/GetExercisesByEquipment?equipment=trap%20bar&offset=0&limit=10' \
  --header 'Content-Type: application/json' \
  --header 'x-rapidapi-host: exercisedb-13001.p.rapidapi.com' \
  --header 'x-rapidapi-key: <YOUR_API_KEY>' \
  --data '{"key1":"value","key2":"value"}'
```

### Query parameters

- `equipment`
  - equipment name, for example `trap bar`
- `offset`
  - pagination starting index
- `limit`
  - number of items to fetch
  - **maximum allowed limit: 25**

### Backend usage guidance

This is one of the most important endpoints for plan generation.

Use this first when the user has strict equipment limits, for example:
- dumbbell only
- barbell only
- body weight only
- bench + dumbbells
- machine-based gym equipment

It helps ensure the AI only receives exercises the user can actually perform.

---

## Endpoint 5: GetAllMuscles

### Purpose

Get the list of all muscles supported by the API.

### Example request

```bash
curl --request POST \
  --url 'https://exercisedb-13001.p.rapidapi.com/api/exercises1/GetAllMuscles' \
  --header 'Content-Type: application/json' \
  --header 'x-rapidapi-host: exercisedb-13001.p.rapidapi.com' \
  --header 'x-rapidapi-key: <YOUR_API_KEY>' \
  --data '{"key1":"value","key2":"value"}'
```

### Backend usage guidance

Use this endpoint for:
- validating allowed muscle values
- building internal mappings
- populating dropdowns or cached metadata
- avoiding invalid muscle names in later requests

### Recommendation

Cache this metadata because it likely changes much less often than exercise search results.

---

## Endpoint 6: GetAllEquipments

### Purpose

Get the list of all equipment values supported by the API.

### Example request

```bash
curl --request POST \
  --url 'https://exercisedb-13001.p.rapidapi.com/api/exercises1/GetAllEquipments' \
  --header 'Content-Type: application/json' \
  --header 'x-rapidapi-host: exercisedb-13001.p.rapidapi.com' \
  --header 'x-rapidapi-key: <YOUR_API_KEY>' \
  --data '{"key1":"value","key2":"value"}'
```

### Backend usage guidance

Use this endpoint for:
- validating equipment values
- building UI filters
- mapping user-selected equipment to API-supported values
- caching metadata for backend planning logic

### Recommendation

Cache this metadata.

---

## Endpoint 7: GetAllBodyparts

### Purpose

Get the list of all body parts supported by the API.

### Example request

```bash
curl --request POST \
  --url 'https://exercisedb-13001.p.rapidapi.com/api/exercises1/GetAllBodyparts' \
  --header 'Content-Type: application/json' \
  --header 'x-rapidapi-host: exercisedb-13001.p.rapidapi.com' \
  --header 'x-rapidapi-key: <YOUR_API_KEY>' \
  --data '{"key1":"value","key2":"value"}'
```

### Backend usage guidance

Use this endpoint for:
- validating body-part filters
- caching metadata
- building broader category mappings for the planner

### Recommendation

Cache this metadata.

---

## How These Endpoints Fit Into Plan Generation

### Suggested usage order

1. collect user data in chat
2. normalize the user's planning profile
3. determine rules such as:
   - split type
   - weekly structure
   - exercise count per day
   - allowed difficulty
4. fetch exercise candidates using these APIs
5. filter candidates by:
   - equipment
   - body part
   - muscle
   - limitations
6. send the filtered shortlist to the AI
7. receive a structured plan from the AI
8. validate it
9. store only the needed plan data in the database

---

## Recommended Fetch Strategy

### For strict equipment users

Start with:
- `GetExercisesByEquipment`

Then narrow further if needed with:
- `GetExercisesByMuscle`
- `GetExercisesByBodyparts`

### For exercise swapping

Use:
- `SearchExercises`
- optionally equipment/bodypart filtering logic after fetch

### For metadata validation

Use:
- `GetAllMuscles`
- `GetAllEquipments`
- `GetAllBodyparts`

---

## Important Storage Guidance

The backend should store the generated plan in the database for the related user, but it should store **only needed information**.

### Store needed plan information such as

- plan id
- user id
- plan name
- goal
- split type
- days per week
- session duration
- active status
- weekly day structure
- exercise name
- optional third-party exercise id
- sets
- reps
- rest
- order
- simple notes

### Do not store unnecessary information such as

- the full raw third-party API response
- full unfiltered exercise candidate lists
- unused metadata copied into every plan
- unnecessary duplicate values already stored elsewhere

---

## Example Backend Integration Notes

### Example service responsibilities

#### exercise-api-service
Responsible for:
- building URLs
- sending RapidAPI requests
- injecting headers
- enforcing max limit 25
- normalizing API responses
- handling pagination

#### exercise-filter-service
Responsible for:
- filtering results by equipment relevance
- removing duplicates
- matching exercises to user restrictions
- grouping exercises for the AI prompt

#### ai-plan-generator
Responsible for:
- taking normalized user profile + filtered exercise pool
- sending structured prompt to AI
- receiving structured weekly plan JSON

---

## Required Implementation Notes For Cursor

When implementing the backend integration, make sure to:

- keep third-party API logic in a dedicated service
- enforce the rule that `limit` must never exceed `25`
- always pass `offset` and `limit` explicitly
- normalize third-party data before using it in AI prompts
- never expose the RapidAPI key to the frontend
- store only necessary plan data in the DB
- design for one user having many plans
- support one active main plan per user
- support a weekly repeating plan structure

---

## Final Summary

These APIs are supporting data sources for exercise selection.
They should not be the only source of logic.

The backend should:
- collect user needs through chat
- derive plan rules
- fetch and filter exercises
- let the AI assemble the final weekly plan
- store only the required plan data
- return a plan card to the frontend
- allow the user to open, edit, and activate a plan

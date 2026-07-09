# ReservationApp – Client

React 19 + TypeScript frontend for a tennis court reservation system. The backend runs on `http://localhost:8080`.

## Requirements

- Node.js 18+
- npm

## Installation

```bash
npm install
```

## Commands

```bash
npm run dev        # Start Vite dev server (http://localhost:5173)
npm run build      # Type-check + build for production
npm run lint       # Run ESLint
npm run preview    # Preview production build

# Regenerate API client from OpenAPI spec (after backend changes)
npx tsx scripts/apigen.ts
```

## Architecture

### Routing

`App.tsx` uses React Router v7. There are two main branches:

| Prefix | Layout | Access |
|---|---|---|
| `/*` | `PublicLayout` | Public |
| `/admin/*` | `AdminLayout` | Admin only (guarded by `AdminRoute`) |

**Public routes** (`PublicLayout`):

| Path | Page |
|---|---|
| `/` | Home |
| `/rezervace` | Reservation |
| `/kurty` | Courts |
| `/arealy` | Venues |
| `/treneri` | Coaches |
| `/o-nas` | About Us |
| `/kontakty` | Contacts |
| `/login` | Login |
| `/registrace` | Registration |
| `/profil` | User profile |

**Admin routes** (`AdminLayout`, requires ADMIN role):

| Path | Page |
|---|---|
| `/admin/` | Admin dashboard |
| `/admin/uzivatele` | User management |
| `/admin/uzivatele/novy` | Create new user |
| `/admin/uzivatele/upravit/:id` | Edit user |
| `/admin/uzivatele/:id` | User detail |
| `/admin/rezervace` | Reservation management |
| `/admin/arealy` | Venue management |
| `/admin/arealy/novy` | Create new venue |
| `/admin/arealy/upravit/:id` | Edit venue |
| `/admin/arealy/:id` | Venue detail |
| `/admin/kurty` | Court management |
| `/admin/kurty/novy` | Create new court |
| `/admin/kurty/:id` | Court detail |
| `/admin/treneri` | Coach management |
| `/admin/info` | About Us / Info page |
| `/admin/info/upravit` | Edit About Us / Info |
| `/admin/kontakty/upravit` | Edit contacts |
| `/admin/profil` | Admin profile |

### Authentication

- JWT token is stored in `localStorage`
- `AuthProvider` configures the HTTP client — attaches `Authorization: Bearer <token>` header to every request
- On a `401` response, the refresh token flow is triggered automatically (`useRefresh`)
- `AdminRoute` protects the admin section — redirects unauthenticated or non-admin users

### API Client

The entire API client (`src/api/`) is **auto-generated** from `docs/api-docs.yaml` using `@hey-api/openapi-ts`. Do not edit files in `src/api/` manually. After changing the OpenAPI spec, run:

```bash
npx tsx scripts/apigen.ts
```

The base URL is configured in `AuthProvider` as `http://localhost:8080/`.

### Server State

TanStack Query v5 is integrated — generated hooks are available in `src/api/@tanstack/react-query.gen.ts`.

### UI

React Bootstrap v2 + Bootstrap 5. No Tailwind or custom CSS framework.

- **yet-another-react-lightbox** — used in venue management for fullscreen photo preview
- **@tanstack/react-form** — used for form state management (e.g. venue creation form)
- **@tinymce/tinymce-react** — rich text editor (TinyMCE), used in About Us / Info editing; self-hosted via `/public/tinymce/`

### Components & Hooks

| File | Description |
|---|---|
| `AuthProvider.tsx` | API client configuration, JWT authentication |
| `AdminRoute.tsx` | Guard for the admin section |
| `NavLinks.tsx` | Navigation for the public layout |
| `AdminNavLinks.tsx` | Navigation for the admin layout |
| `FlashMessages.tsx` | Flash message display |
| `Loading.tsx` | Loading indicator |
| `useAuth.ts` | Hook for accessing authentication state |
| `useApi.ts` | Hook for calling API services |
| `useRefresh.ts` | Hook for refreshing the JWT token |
| `useDateFormat.ts` | Hook for date formatting |
| `DateFormat.ts` | Utility function for formatting dates in Czech locale |
| `Filter.tsx` | Collapsible filter/form component built with `@tanstack/react-form` |
| `FormFields.tsx` | Reusable `@tanstack/react-form` field wrappers (e.g. `FormInput`) |
| `MyEditor.tsx` | TinyMCE rich text editor wrapper for use in forms |
| `MySerializer.ts` | Serializes a plain object to `multipart/form-data` (handles `File`/`Blob` fields) |
| `constant/constant.ts` | Shared constants — exports `API_URL` (`http://localhost:8080`) |

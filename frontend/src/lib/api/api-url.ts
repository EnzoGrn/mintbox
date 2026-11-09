/**
 * Where the backend API runs, seen from the frontend's server. Defaults to the backend
 * started with `./mvnw spring-boot:run`.
 *
 * Read when the server renders a page, and at build time for the /api/* forwarding rule
 * (next.config.ts): changing it means rebuilding.
 */
export const apiUrl = process.env.MINTBOX_API_URL ?? "http://localhost:8080";

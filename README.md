"# estate-backend"

## Deploy to Render

This repository includes a Docker-based Render Blueprint in `render.yaml`. Create a Blueprint in Render from this GitHub repository and provide these values when prompted:

- `DB_URL`: a reachable MySQL JDBC URL, for example `jdbc:mysql://HOST:3306/DATABASE?sslMode=REQUIRED&serverTimezone=UTC`.
- `DB_USERNAME` and `DB_PASSWORD`: the hosted MySQL credentials.
- `APP_CORS_ORIGINS`: the deployed frontend origin, such as `https://your-frontend.onrender.com`.

Render generates `JWT_SECRET` automatically. The web service uses Render's `PORT` environment variable. The included free web plan is for testing and spins down when idle; uploaded files use ephemeral local storage and can disappear on restart or redeploy. Use persistent storage before accepting client uploads in production.

import api from "src/api/api";

export async function initUser() {
  await api.get("users/me")
}

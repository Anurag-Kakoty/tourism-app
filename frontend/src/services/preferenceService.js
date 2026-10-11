import api from "./api";
import API from "../constants/api";

const preferenceService = {
  async getPreferences() {
    const response = await api.get(API.USER_PREFERENCES);
    return response.data;
  },

  async updatePreferences(tagIds) {
    const response = await api.put(API.USER_PREFERENCES, {
      tagIds,
    });
    return response.data;
  },

  async skipPreferences() {
    const response = await api.post(`${API.USER_PREFERENCES}/skip`);
    return response.data;
  },
};

export default preferenceService;

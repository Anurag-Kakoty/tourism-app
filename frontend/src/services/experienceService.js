import api from "./api";
import API from "../constants/api";

const experienceService = {
  async getAll() {
    const response = await api.get(API.EXPERIENCES);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.EXPERIENCES}/${id}`
    );

    return response.data;
  },
};

export default experienceService;
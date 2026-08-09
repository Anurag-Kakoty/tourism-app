import api from "./api";
import API from "../constants/api";

const festivalService = {
  async getAll() {
    const response = await api.get(API.FESTIVALS);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`${API.FESTIVALS}/${id}`);
    return response.data;
  },
};

export default festivalService;
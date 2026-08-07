import api from "./api";
import API from "../constants/api";

const stateService = {
  async getAll() {
    const response = await api.get(API.STATES);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`${API.STATES}/${id}`);
    return response.data;
  },
};

export default stateService;
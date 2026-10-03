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

  async create(stateData) {
    const response = await api.post(
      API.STATES,
      stateData
    );

    return response.data;
  },

  async update(id, stateData) {
    const response = await api.put(
      `${API.STATES}/${id}`,
      stateData
    );

    return response.data;
  },

  async delete(id) {
    await api.delete(`${API.STATES}/${id}`);
  },
};

export default stateService;
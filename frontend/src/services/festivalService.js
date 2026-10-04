import api from "./api";
import API from "../constants/api";

const festivalService = {
  async getAll() {
    const response = await api.get(API.FESTIVALS);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.FESTIVALS}/${id}`
    );

    return response.data;
  },

  async create(festivalData) {
    const response = await api.post(
      API.FESTIVALS,
      festivalData
    );

    return response.data;
  },

  async update(id, festivalData) {
    const response = await api.put(
      `${API.FESTIVALS}/${id}`,
      festivalData
    );

    return response.data;
  },

  async delete(id) {
    await api.delete(
      `${API.FESTIVALS}/${id}`
    );
  },
};

export default festivalService;
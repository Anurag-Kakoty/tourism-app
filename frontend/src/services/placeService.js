import api from "./api";
import API from "../constants/api";

const placeService = {
  async getAll() {
    const response = await api.get(API.ATTRACTIONS);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`${API.ATTRACTIONS}/${id}`);
    return response.data;
  },
};

export default placeService;
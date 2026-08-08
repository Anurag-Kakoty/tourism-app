import api from "./api";
import API from "../constants/api";

const destinationService = {
  async getAll() {
    const response = await api.get(API.DESTINATIONS);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`${API.DESTINATIONS}/${id}`);
    return response.data;
  },
};

export default destinationService;
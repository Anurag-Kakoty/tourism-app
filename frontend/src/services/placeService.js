import api from "./api";
import API from "../constants/api";

const placeService = {
  async getAll(filters = {}) {
    const response = await api.get(API.ATTRACTIONS, {
      params: filters,
    });

    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.ATTRACTIONS}/${id}`
    );

    return response.data;
  },

  async getByDestination(destinationId) {
    const response = await api.get(API.ATTRACTIONS, {
      params: {
        destinationId,
      },
    });

    return response.data;
  },
};

export default placeService;
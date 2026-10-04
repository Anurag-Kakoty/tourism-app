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

  async create(attractionData) {
    const response = await api.post(
      API.ATTRACTIONS,
      attractionData
    );

    return response.data;
  },

  async update(id, attractionData) {
    const response = await api.put(
      `${API.ATTRACTIONS}/${id}`,
      attractionData
    );

    return response.data;
  },

  async delete(id) {
    await api.delete(
      `${API.ATTRACTIONS}/${id}`
    );
  },
};

export default placeService;
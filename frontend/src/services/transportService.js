import api from "./api";
import API from "../constants/api";

const transportService = {
  async getAll(filters = {}) {
    const response = await api.get(API.TRANSPORT, {
      params: filters,
    });

    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.TRANSPORT}/${id}`
    );

    return response.data;
  },

  async getByDestination(destinationId) {
    const response = await api.get(
      API.TRANSPORT,
      {
        params: {
          destinationId,
        },
      }
    );

    return response.data;
  },

  async getByType(type) {
    const response = await api.get(
      API.TRANSPORT,
      {
        params: {
          type,
        },
      }
    );

    return response.data;
  },

  async getByAvailability(available = true) {
    const response = await api.get(
      API.TRANSPORT,
      {
        params: {
          available,
        },
      }
    );

    return response.data;
  },
};

export default transportService;
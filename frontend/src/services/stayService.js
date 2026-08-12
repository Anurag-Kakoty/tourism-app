import api from "./api";
import API from "../constants/api";

const stayService = {
  async getAll() {
    const response = await api.get(API.ACCOMMODATIONS);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.ACCOMMODATIONS}/${id}`
    );

    return response.data;
  },

  async getByDestination(destinationId) {
    const response = await api.get(
      API.ACCOMMODATIONS,
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
      API.ACCOMMODATIONS,
      {
        params: {
          type,
        },
      }
    );

    return response.data;
  },

  async getAvailable(available = true) {
    const response = await api.get(
      API.ACCOMMODATIONS,
      {
        params: {
          available,
        },
      }
    );

    return response.data;
  },

  async getByDestinationAndType(destinationId, type) {
    const response = await api.get(
      API.ACCOMMODATIONS,
      {
        params: {
          destinationId,
          type,
        },
      }
    );

    return response.data;
  },
};

export default stayService;
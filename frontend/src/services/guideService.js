import api from "./api";
import API from "../constants/api";

const guideService = {
  async getAll() {
    const response = await api.get(API.GUIDES);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.GUIDES}/${id}`
    );

    return response.data;
  },

  async getByDestination(destinationId) {
    const response = await api.get(
      API.GUIDES,
      {
        params: {
          destinationId,
        },
      }
    );

    return response.data;
  },

  async getByAvailability(available = true) {
    const response = await api.get(
      API.GUIDES,
      {
        params: {
          available,
        },
      }
    );

    return response.data;
  },

  async getByProvidesTransport(providesTransport = true) {
    const response = await api.get(
      API.GUIDES,
      {
        params: {
          providesTransport,
        },
      }
    );

    return response.data;
  },

  async getByLanguage(language) {
    const response = await api.get(
      API.GUIDES,
      {
        params: {
          language,
        },
      }
    );

    return response.data;
  },
};

export default guideService;
import api from "./api";
import API from "../constants/api";

const guideService = {
  async getAll(filters = {}) {
    const params = {};

    if (filters.destinationId) {
      params.destinationId = filters.destinationId;
    }

    if (filters.available !== "") {
      params.available = filters.available;
    }

    if (filters.providesTransport !== "") {
      params.providesTransport = filters.providesTransport;
    }

    if (filters.language) {
      params.language = filters.language;
    }

    const response = await api.get(API.GUIDES, {
      params,
    });

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
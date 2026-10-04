import api from "./api";
import API from "../constants/api";

const stayService = {
  async getAll(filters = {}) {
    const response = await api.get(
      API.ACCOMMODATIONS,
      {
        params: filters,
      }
    );

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

  async getByDestinationAndType(
    destinationId,
    type
  ) {
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

  async create(accommodationData) {
    const response = await api.post(
      API.ACCOMMODATIONS,
      accommodationData
    );

    return response.data;
  },

  async update(id, accommodationData) {
    const response = await api.put(
      `${API.ACCOMMODATIONS}/${id}`,
      accommodationData
    );

    return response.data;
  },

  async delete(id) {
    await api.delete(
      `${API.ACCOMMODATIONS}/${id}`
    );
  },
};

export default stayService;
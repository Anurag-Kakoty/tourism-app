import api from "./api";
import API from "../constants/api";

const festivalOccurrenceService = {
  async getAll() {
    const response = await api.get(API.FESTIVAL_OCCURRENCES);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.FESTIVAL_OCCURRENCES}/${id}`
    );

    return response.data;
  },

  async getByState(stateId) {
    const response = await api.get(
      API.FESTIVAL_OCCURRENCES,
      {
        params: {
          stateId,
        },
      }
    );

    return response.data;
  },

  async getByYear(year) {
    const response = await api.get(
      API.FESTIVAL_OCCURRENCES,
      {
        params: {
          year,
        },
      }
    );

    return response.data;
  },

  async getByStateAndYear(stateId, year) {
    const response = await api.get(
      API.FESTIVAL_OCCURRENCES,
      {
        params: {
          stateId,
          year,
        },
      }
    );

    return response.data;
  },

  async getUpcoming() {
    const response = await api.get(
      `${API.FESTIVAL_OCCURRENCES}/upcoming`
    );

    return response.data;
  },
};

export default festivalOccurrenceService;
import api from "./api";
import API from "../constants/api";

const destinationService = {
  async getAll(filters = {}) {
    const response = await api.get(API.DESTINATIONS, {
      params: {
        stateId: filters.stateId || undefined,
        type: filters.type || undefined,
        featured:
          filters.featured !== "" && filters.featured !== undefined
            ? filters.featured
            : undefined,
        popular:
          filters.popular !== "" && filters.popular !== undefined
            ? filters.popular
            : undefined,
      },
    });

    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.DESTINATIONS}/${id}`
    );

    return response.data;
  },

  async getByState(stateId) {
    return this.getAll({
      stateId,
    });
  },
};

export default destinationService;
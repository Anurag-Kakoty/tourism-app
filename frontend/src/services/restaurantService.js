import api from "./api";
import API from "../constants/api";

const restaurantService = {
  async getAll(filters = {}) {
    const response = await api.get(API.RESTAURANTS, {
      params: filters,
    });

    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.RESTAURANTS}/${id}`
    );

    return response.data;
  },

  async getByDestination(destinationId) {
    const response = await api.get(
      API.RESTAURANTS,
      {
        params: {
          destinationId,
        },
      }
    );

    return response.data;
  },

  async getByCuisine(cuisine) {
    const response = await api.get(
      API.RESTAURANTS,
      {
        params: {
          cuisine,
        },
      }
    );

    return response.data;
  },

  async getByVegetarian(vegetarian = true) {
    const response = await api.get(
      API.RESTAURANTS,
      {
        params: {
          vegetarian,
        },
      }
    );

    return response.data;
  },
};

export default restaurantService;
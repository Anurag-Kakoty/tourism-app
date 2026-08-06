import api from "./api";

const placeService = {
  async getAll() {
    const response = await api.get("/attractions");
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`/attractions/${id}`);
    return response.data;
  },
};

export default placeService;
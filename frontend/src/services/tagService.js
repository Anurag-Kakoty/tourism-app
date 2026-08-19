import api from "./api";
import API from "../constants/api";

const tagService = {
  async getAll() {
    const response = await api.get(API.TAGS);
    return response.data;
  },

  async getById(id) {
    const response = await api.get(
      `${API.TAGS}/${id}`
    );

    return response.data;
  },
};

export default tagService;
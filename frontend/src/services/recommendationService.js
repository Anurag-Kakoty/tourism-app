import api from "./api";
import API from "../constants/api";

const recommendationService = {
  async getAttractionRecommendations(params = {}) {
    const response = await api.get(
      `${API.RECOMMENDATIONS}/attractions`,
      {
        params,
      }
    );
    return response.data;
  },
};

export default recommendationService;

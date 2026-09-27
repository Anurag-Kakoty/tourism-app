import api from "./api";

const generateAiItinerary = async (request) => {
  const response = await api.post(
    "/ai/itineraries/generate",
    request
  );

  return response.data;
};

export default {
  generateAiItinerary,
};
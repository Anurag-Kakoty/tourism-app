import api from "./api";

const createItinerary = async (request) => {
  const response = await api.post(
    "/itineraries",
    request
  );

  return response.data;
};

const addItineraryItem = async (
  itineraryId,
  request
) => {
  const response = await api.post(
    `/itineraries/${itineraryId}/items`,
    request
  );

  return response.data;
};

const getItineraryById = async (id) => {
  const response = await api.get(
    `/itineraries/${id}`
  );

  return response.data;
};

const getAllItineraries = async () => {
  const response = await api.get(
    "/itineraries"
  );

  return response.data;
};

const updateItinerary = async (
  id,
  request
) => {
  const response = await api.put(
    `/itineraries/${id}`,
    request
  );

  return response.data;
};

const deleteItinerary = async (id) => {
  await api.delete(
    `/itineraries/${id}`
  );
};

export default {
  createItinerary,
  addItineraryItem,
  getItineraryById,
  getAllItineraries,
  updateItinerary,
  deleteItinerary,
};
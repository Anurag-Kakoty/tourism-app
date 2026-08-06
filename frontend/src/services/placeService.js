import places from "../data/places";

const placeService = {
  getAll() {
    return places;
  },

  getById(id) {
    return places.find((place) => place.id === Number(id));
  },
};

export default placeService;
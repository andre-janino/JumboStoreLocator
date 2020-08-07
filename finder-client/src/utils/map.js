// My personal google maps API key for this project, use responsibly
const API_KEY = `AIzaSyBHKgvZ_5ItItbQhSMFaJgNeFaMnzVnlho`;
const MAP_CALLBACK = `gmapsCallback`;

let initialized = !!window.google;
let resolveInitPromise;
let rejectInitPromise;

// google maps initialization promise
const initPromise = new Promise((resolve, reject) => {
  resolveInitPromise = resolve;
  rejectInitPromise = reject;
});

export default function init() {
  // only run this method once
  if (initialized) return initPromise;
  initialized = true;

  // callback called by google maps if it is successfully loaded
  window[MAP_CALLBACK] = () => resolveInitPromise(window.google);

  // inject a script tag into the head of the page to load google map
  const script = document.createElement(`script`);
  script.async = true;
  script.defer = true;
  script.src = `https://maps.googleapis.com/maps/api/js?key=${API_KEY}&callback=${MAP_CALLBACK}&sensor=false&libraries=places`;
  script.onerror = rejectInitPromise;
  document.querySelector(`head`).appendChild(script);

  return initPromise;
}
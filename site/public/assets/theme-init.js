(function () {
  "use strict";

  try {
    var savedTheme = localStorage.getItem("vevolt-theme");
    if (savedTheme === "light" || savedTheme === "dark") {
      document.documentElement.dataset.theme = savedTheme;
    }
  } catch (error) {
    // The system preference remains the fallback when storage is unavailable.
  }
})();

(function () {
  "use strict";

  var toggle = document.querySelector("[data-menu-toggle]");
  var panel = document.querySelector("[data-menu-panel]");
  var themeToggle = document.querySelector("[data-theme-toggle]");

  function currentTheme() {
    return (
      document.documentElement.dataset.theme ||
      (window.matchMedia("(prefers-color-scheme: light)").matches
        ? "light"
        : "dark")
    );
  }

  function updateThemeButton() {
    if (!themeToggle) return;
    var theme = currentTheme();
    var nextIsLight = theme === "dark";
    themeToggle.textContent = String.fromCodePoint(nextIsLight ? 9788 : 9790);
    themeToggle.setAttribute(
      "aria-label",
      nextIsLight
        ? themeToggle.dataset.labelLight
        : themeToggle.dataset.labelDark,
    );
    themeToggle.setAttribute(
      "title",
      nextIsLight
        ? themeToggle.dataset.labelLight
        : themeToggle.dataset.labelDark,
    );
    var themeColor = document.querySelector('meta[name="theme-color"]');
    if (themeColor)
      themeColor.setAttribute(
        "content",
        theme === "light" ? "#f4f7fb" : "#080d16",
      );
  }

  if (themeToggle) {
    updateThemeButton();
    themeToggle.addEventListener("click", function () {
      var next = currentTheme() === "dark" ? "light" : "dark";
      document.documentElement.dataset.theme = next;
      try {
        localStorage.setItem("vevolt-theme", next);
      } catch (error) {}
      updateThemeButton();
    });
  }

  function initializeGallery(gallery) {
    var items = Array.prototype.slice.call(
      gallery.querySelectorAll("figure"),
    );
    var navigation = gallery.nextElementSibling;
    if (!navigation || !navigation.matches("[data-gallery-navigation]"))
      return;

    var previous = navigation.querySelector("[data-gallery-previous]");
    var next = navigation.querySelector("[data-gallery-next]");
    var dotsContainer = navigation.querySelector("[data-gallery-dots]");
    if (!previous || !next || !dotsContainer || items.length < 2) {
      navigation.hidden = true;
      return;
    }

    var currentIndex = 0;
    var dots = items.map(function (_, index) {
      var dot = document.createElement("button");
      dot.className = "gallery-dot";
      dot.type = "button";
      dot.setAttribute(
        "aria-label",
        gallery.dataset.slideLabel + " " + String(index + 1),
      );
      dot.addEventListener("click", function () {
        goTo(index);
      });
      dotsContainer.appendChild(dot);
      return dot;
    });

    function update(index) {
      currentIndex = Math.max(0, Math.min(index, items.length - 1));
      previous.disabled = currentIndex === 0;
      next.disabled = currentIndex === items.length - 1;
      dots.forEach(function (dot, dotIndex) {
        dot.setAttribute(
          "aria-current",
          dotIndex === currentIndex ? "true" : "false",
        );
      });
    }

    function goTo(index) {
      var targetIndex = Math.max(0, Math.min(index, items.length - 1));
      update(targetIndex);
      gallery.scrollTo({
        left: items[targetIndex].offsetLeft - items[0].offsetLeft,
        behavior: window.matchMedia("(prefers-reduced-motion: reduce)").matches
          ? "auto"
          : "smooth",
      });
    }

    function nearestIndex() {
      var closestIndex = 0;
      var closestDistance = Infinity;
      items.forEach(function (item, index) {
        var distance = Math.abs(
          item.offsetLeft - items[0].offsetLeft - gallery.scrollLeft,
        );
        if (distance < closestDistance) {
          closestDistance = distance;
          closestIndex = index;
        }
      });
      return closestIndex;
    }

    var framePending = false;
    gallery.addEventListener(
      "scroll",
      function () {
        if (framePending) return;
        framePending = true;
        window.requestAnimationFrame(function () {
          update(nearestIndex());
          framePending = false;
        });
      },
      { passive: true },
    );
    previous.addEventListener("click", function () {
      goTo(currentIndex - 1);
    });
    next.addEventListener("click", function () {
      goTo(currentIndex + 1);
    });
    window.addEventListener("resize", function () {
      update(nearestIndex());
    });
    update(0);
  }

  document.querySelectorAll("[data-gallery]").forEach(initializeGallery);

  if (!toggle || !panel) return;

  function closeMenu() {
    toggle.setAttribute("aria-expanded", "false");
    panel.dataset.open = "false";
    document.body.classList.remove("menu-open");
  }

  toggle.addEventListener("click", function () {
    var open = toggle.getAttribute("aria-expanded") === "true";
    toggle.setAttribute("aria-expanded", String(!open));
    panel.dataset.open = String(!open);
    document.body.classList.toggle("menu-open", !open);
  });

  panel.addEventListener("click", function (event) {
    if (event.target.closest("a")) closeMenu();
  });

  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
      closeMenu();
      toggle.focus();
    }
  });

  window.addEventListener("resize", function () {
    if (window.innerWidth > 1040) closeMenu();
  });
})();

(() => {
    const form = document.querySelector("#search-form");
    const viewInput = document.querySelector("#view-input");
    const mapElement = document.querySelector("#place-map");
    let map;

    function showView(view) {
        const list = document.querySelector("#list-view");
        const mapPanel = document.querySelector("#map-view");
        if (!list || !mapPanel) return;
        const mapActive = view === "map";
        list.classList.toggle("is-hidden", mapActive);
        mapPanel.classList.toggle("is-hidden", !mapActive);
        document.querySelectorAll(".view-button").forEach((button) => {
            const active = button.dataset.view === view;
            button.classList.toggle("active", active);
            button.setAttribute("aria-pressed", String(active));
        });
        if (viewInput) viewInput.value = view;
        if (mapActive) {
            initializeMap();
            if (map) window.requestAnimationFrame(() => map.invalidateSize());
        }
    }

    function initializeMap() {
        if (!mapElement || map) return;
        if (typeof L === "undefined") {
            document.querySelector(".map-fallback")?.classList.remove("is-hidden");
            return;
        }
        const cards = [...document.querySelectorAll(".place-card[data-lat][data-lng]")];
        if (cards.length === 0) {
            const emptyMessage = document.createElement("div");
            emptyMessage.className = "map-empty-message";
            emptyMessage.textContent = "No Bangalore places in this search yet. Try changing a filter.";
            mapElement.replaceChildren(emptyMessage);
            map = null;
            return;
        }
        map = L.map(mapElement, { scrollWheelZoom: false }).setView([12.9716, 77.5946], 12);
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);
        const bounds = [];
        cards.forEach((card) => {
            const latitude = Number(card.dataset.lat);
            const longitude = Number(card.dataset.lng);
            if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) return;
            const point = [latitude, longitude];
            bounds.push(point);
            const marker = L.marker(point).addTo(map);
            const popup = document.createElement("div");
            popup.className = "map-popup";
            const title = document.createElement("strong");
            title.textContent = card.dataset.name || "Public place";
            const address = document.createElement("span");
            address.textContent = card.dataset.address || "";
            const details = document.createElement("a");
            details.textContent = "Place details →";
            details.href = `/places/${encodeURIComponent(card.dataset.id)}`;
            const route = document.createElement("a");
            route.textContent = "Plan a route ↗";
            const placeUrl = `https://www.openstreetmap.org/?mlat=${latitude}&mlon=${longitude}#map=16/${latitude}/${longitude}`;
            route.href = placeUrl;
            route.target = "_blank";
            route.rel = "noreferrer";
            route.addEventListener("click", (event) => {
                if (!navigator.geolocation) return;
                event.preventDefault();
                const directionsTab = window.open("about:blank", "_blank");
                if (!directionsTab) {
                    window.location.assign(placeUrl);
                    return;
                }
                directionsTab.opener = null;
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const origin = `${position.coords.latitude},${position.coords.longitude}`;
                        directionsTab.location.href = `https://www.openstreetmap.org/directions?engine=fossgis_osrm_car&route=${encodeURIComponent(`${origin};${latitude},${longitude}`)}`;
                    },
                    () => { directionsTab.location.href = placeUrl; },
                    { enableHighAccuracy: false, maximumAge: 60000, timeout: 8000 }
                );
            });
            popup.append(title, address, details, route);
            marker.bindPopup(popup);
        });
        if (bounds.length > 1) map.fitBounds(bounds, { padding: [36, 36], maxZoom: 15 });
        else if (bounds.length === 1) map.setView(bounds[0], 15);
    }

    document.querySelectorAll(".view-button").forEach((button) => {
        button.addEventListener("click", () => {
            const view = button.dataset.view;
            if (!form) {
                showView(view);
                return;
            }
            if (viewInput) viewInput.value = view;
            form.requestSubmit();
        });
    });

    if (mapElement && !document.querySelector("#list-view")?.classList.contains("is-hidden")) {
        // Defer creating map tiles until the visitor chooses map view.
    } else if (mapElement) {
        initializeMap();
    }

    document.querySelectorAll("[data-photo-input]").forEach((input) => {
        input.addEventListener("change", () => {
            const preview = document.querySelector("[data-photo-preview]");
            const file = input.files && input.files[0];
            if (!preview) return;
            preview.replaceChildren();
            if (!file) {
                preview.classList.add("is-hidden");
                return;
            }
            if (!["image/jpeg", "image/png", "image/webp"].includes(file.type)) {
                input.value = "";
                preview.textContent = "Please choose a JPG, PNG, or WebP image.";
                preview.classList.remove("is-hidden");
                return;
            }
            const image = document.createElement("img");
            image.alt = "Selected photo preview";
            image.src = URL.createObjectURL(file);
            image.addEventListener("load", () => URL.revokeObjectURL(image.src), { once: true });
            preview.append(image);
            preview.classList.remove("is-hidden");
        });
    });

    document.querySelectorAll(".notice").forEach((notice) => {
        window.setTimeout(() => notice.classList.add("notice-dismissed"), 6500);
    });
})();

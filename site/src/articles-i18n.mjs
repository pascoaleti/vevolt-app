import { articles as portugueseArticles } from "./articles.mjs";

function materialize(base, entry, localeKey) {
  return {
    ...base,
    ...entry,
    image: base.image.replace("/pt-", `/${localeKey}-`),
    sections: entry.sectionHeadings.map((heading, index) => ({
      heading,
      paragraphs: [entry.paragraphs[index]],
      ...(index === 1 && entry.bullets ? { bullets: entry.bullets } : {}),
    })),
    sources: base.sources,
  };
}

const en = [
  {
    slug: "ev-charging-station-near-me",
    title: "EV charging station near me: how to find and verify one",
    description: "Learn how to find nearby EV chargers and check connector, power, access, price and recent conditions before leaving.",
    keyword: "EV charging station near me",
    imageAlt: "VeVolt map showing nearby EV charging stations",
    lead: "A pin on the map is only the first step. A useful charging stop matches your route, vehicle, available time and the conditions you are likely to find on arrival.",
    sectionHeadings: ["Start with your trip", "Check what can change your decision", "Use community reports with context"],
    paragraphs: [
      "Check your remaining battery, the distance still to travel and whether the stop is urgent or can happen while you do something else. The nearest charger is not always the best charger for that moment.",
      "Open the location details and compare connector, power, address, access hours and tariff when available. For a critical stop, confirm the information with the operator or venue before committing to the detour.",
      "Recent reports can reveal faults, queues, blocked access or a price change. They are valuable context, but they are not official real-time availability, so keep an alternative within reach.",
    ],
    bullets: ["Compatible connector", "Useful charging power", "Access hours and parking rules", "Energy and parking costs", "Recent driver reports"],
    faqs: [["Does VeVolt guarantee that a charger is available?", "No. VeVolt organizes public data and community reports; critical availability should be confirmed with the operator."], ["Should I always choose the nearest charger?", "No. Compatibility, power, access and reliability may make a slightly farther location the better choice."]],
  },
  {
    slug: "how-much-does-it-cost-to-charge-an-electric-car",
    title: "How much does it cost to charge an electric car?",
    description: "Calculate an EV charging estimate using battery capacity, energy price, charging losses, parking fees and session charges.",
    keyword: "cost to charge an electric car",
    imageAlt: "VeVolt charging cost and savings screen",
    lead: "Charging cost is not one fixed number. It depends on how many kilowatt-hours enter the battery, the local tariff and any fees charged by the station or parking facility.",
    sectionHeadings: ["Use kilowatt-hours as the starting point", "Include the complete cost", "Compare trips, not isolated prices"],
    paragraphs: [
      "Multiply the energy added in kWh by the price per kWh. If you only know the battery percentage, estimate the fraction of the usable battery capacity that needs to be restored.",
      "Allow for charging losses and check session, idle, membership and parking fees. A location advertising free energy can still have a meaningful parking cost.",
      "Record energy and amount paid after each session. A small history gives a more realistic cost per kilometer than a single ideal calculation.",
    ],
    bullets: ["Energy delivered in kWh", "Price per kWh", "Charging losses", "Session or idle fee", "Parking charge"],
    faqs: [["Is public charging always more expensive than home charging?", "Not always, but public fast charging often includes infrastructure and service costs that can raise the price."], ["Can VeVolt charge me for electricity?", "No. VeVolt helps organize information and records; payment is handled by the charger operator or venue."]],
  },
  {
    slug: "how-long-does-it-take-to-charge-an-electric-car",
    title: "How long does it take to charge an electric car?",
    description: "Understand how charger power, vehicle limits, battery level and temperature affect electric-car charging time.",
    keyword: "electric car charging time",
    imageAlt: "Electric car charging information in VeVolt",
    lead: "The number printed on a charger is not a promise of constant power. The vehicle, battery and charging curve determine how quickly energy is actually received.",
    sectionHeadings: ["Make a useful first estimate", "Understand the charging curve", "Plan around the time you really have"],
    paragraphs: [
      "Divide the energy you need to add by the effective charging power. A 30 kWh top-up at an average 30 kW would take roughly one hour before losses and power variation.",
      "Fast charging usually slows as the battery fills, especially near a high state of charge. Temperature, battery conditioning and shared power can also reduce the average.",
      "For a short stop, focus on the range needed for the next leg instead of automatically charging to 100%. This can save time and free the charger sooner.",
    ],
    bullets: ["Vehicle AC/DC limit", "Charger power", "Starting battery level", "Target battery level", "Temperature and charging curve"],
    faqs: [["Does a 150 kW charger always deliver 150 kW?", "No. The vehicle and battery decide the power accepted at each moment."], ["Why does charging slow near 100%?", "Battery management reduces power at high charge levels to protect the battery."]],
  },
  {
    slug: "electric-car-connectors-type-2-ccs2-chademo-gbt",
    title: "Electric-car connectors: Type 2, CCS2, CHAdeMO and GB/T",
    description: "Identify common EV charging connectors and verify compatibility before choosing a charging station.",
    keyword: "electric car connector types",
    imageAlt: "VeVolt screen with charging-station connector details",
    lead: "A charger can be close and powerful but still be unusable if its connector does not match the vehicle. Compatibility should be checked before the route begins.",
    sectionHeadings: ["Separate AC from DC charging", "Match the connector to the vehicle", "Check more than the plug shape"],
    paragraphs: [
      "Type 2 is widely associated with AC charging, while CCS2 combines AC pins with additional DC contacts. CHAdeMO and GB/T serve different vehicle and market ecosystems.",
      "Use the vehicle manual and charging-port label as the source of truth. Adapters are not universal and should only be used when approved for the vehicle and charger.",
      "Connector compatibility does not guarantee maximum speed. The car's onboard charger, DC limit and the station's available power still determine performance.",
    ],
    bullets: ["Connector standard", "AC or DC", "Vehicle power limit", "Cable attached or required", "Approved adapter requirements"],
    faqs: [["Is Type 2 the same as CCS2?", "CCS2 includes the Type 2 section but adds DC contacts for fast charging."], ["Can an adapter make every charger compatible?", "No. Electrical standards, communication and manufacturer approval still matter."]],
  },
  {
    slug: "how-to-plan-an-electric-car-trip",
    title: "How to plan an electric-car trip with charging stops",
    description: "Plan an EV road trip using range, compatible chargers, alternatives, charging time and safe arrival margins.",
    keyword: "plan electric car trip",
    imageAlt: "VeVolt route and estimated battery on arrival",
    lead: "A reliable EV trip plan connects vehicle range with real charging options and leaves room for weather, traffic, elevation and unexpected station conditions.",
    sectionHeadings: ["Build the route from the vehicle", "Choose stops with alternatives", "Update the plan during the trip"],
    paragraphs: [
      "Start with current battery, practical range and the connector supported by the vehicle. Use a conservative margin instead of treating the advertised maximum range as guaranteed.",
      "Prefer charging areas with compatible nearby alternatives, useful services and enough power for the planned stop. Verify access hours and operator requirements before departure.",
      "Recheck consumption and estimated arrival battery after each leg. Strong wind, rain, speed, temperature and elevation can change the plan.",
    ],
    bullets: ["Current battery and practical range", "Compatible primary charger", "Nearby backup charger", "Access and operator app", "Arrival reserve"],
    faqs: [["How much battery should remain on arrival?", "There is no universal value; choose a reserve appropriate to the route, weather and availability of alternatives."], ["Does VeVolt replace a navigation app?", "No. VeVolt supports charging decisions and opens the trip in a navigation app for road guidance."]],
  },
  {
    slug: "electric-car-charger-in-condominium",
    title: "Electric-car charging in a condominium: what to organize",
    description: "Understand electrical assessment, approval, safety, scheduling, consumption records and cost allocation for shared EV charging.",
    keyword: "electric car charger condominium",
    imageAlt: "VeVolt Condo shared charging management screen",
    lead: "Installing or sharing chargers in a condominium is not only a hardware decision. Capacity, safety, access rules and transparent cost allocation need to work together.",
    sectionHeadings: ["Begin with technical assessment", "Define shared rules", "Keep records transparent"],
    paragraphs: [
      "A qualified professional should evaluate demand, electrical capacity, protection devices, cable routes and applicable fire-safety requirements before installation.",
      "The condominium should define who can use each charger, how time slots are reserved, what happens during peaks and how incidents are reported.",
      "Manual or metered consumption records should be reviewed consistently. VeVolt Condo can organize participants, chargers, schedules and monthly allocation without physically controlling the equipment.",
    ],
    bullets: ["Electrical capacity study", "Installation and safety standard", "User and visitor access", "Reservation rules", "Consumption and cost allocation"],
    faqs: [["Does VeVolt Condo control the charger?", "No. It organizes people, schedules and records; it does not switch hardware or collect energy payments."], ["Do invited residents need a Condo subscription?", "No. The administrator subscribes and invited residents join the hub free of charge."]],
  },
  {
    slug: "fast-charging-electric-car-best-practices",
    title: "Fast charging an electric car: practical best practices",
    description: "Use DC fast charging efficiently by checking compatibility, battery level, charging curve, queue and stop duration.",
    keyword: "fast charging electric car",
    imageAlt: "Fast-charging location details shown in VeVolt",
    lead: "Fast charging is most useful when it shortens a journey. Good decisions focus on the energy needed for the next leg, not only the charger's peak number.",
    sectionHeadings: ["Arrive prepared", "Charge through the useful part of the curve", "Leave a clear and useful report"],
    paragraphs: [
      "Confirm connector, operator access and payment method before arrival. Battery preconditioning, when supported by the vehicle, can improve charging performance.",
      "Power often falls as the battery approaches a high state of charge. Continuing to 100% may take longer than making another well-placed stop.",
      "Move the vehicle when the planned level is reached and report faults, queues or access problems. This helps the next driver plan with better context.",
    ],
    bullets: ["Compatible DC connector", "Operator account or card", "Battery preconditioning", "Target for the next leg", "Idle-fee awareness"],
    faqs: [["Is frequent fast charging forbidden?", "No, but follow the vehicle manufacturer's battery-care guidance and use the charging method that fits your routine."], ["Why does charging power change during a session?", "Battery level, temperature, vehicle limits and station conditions all affect the charging curve."]],
  },
  {
    slug: "free-or-paid-ev-charging-station",
    title: "Free or paid EV charging station: how to know before arriving",
    description: "Check electricity price, parking charges, session fees, access rules and operator requirements before using an EV charger.",
    keyword: "free EV charging station",
    imageAlt: "VeVolt charging location with tariff information",
    lead: "A charger marked as free may still sit inside paid parking, while a paid charger can include different session or idle fees. Look at the complete stop cost.",
    sectionHeadings: ["Read the tariff carefully", "Confirm access conditions", "Record what was actually paid"],
    paragraphs: [
      "Check whether billing is per kWh, minute, session or membership. Also look for idle fees that begin after charging ends.",
      "Shopping centers, hotels and workplaces may limit charging to customers or guests. Parking and opening hours can matter as much as the energy tariff.",
      "After charging, record energy and total amount. Real records make future comparisons more accurate and can help other drivers when shared responsibly.",
    ],
    bullets: ["Energy tariff", "Session fee", "Idle fee", "Parking cost", "Customer or guest requirement"],
    faqs: [["Does free charging mean free parking?", "No. Energy and parking are separate charges and must be checked independently."], ["Are community prices guaranteed?", "No. They are contextual reports and may change; confirm at the operator or location."]],
  },
  {
    slug: "how-to-check-if-an-ev-charger-is-working",
    title: "How to check whether an EV charger is working",
    description: "Reduce uncertainty by checking operator status, recent driver reports, access conditions and nearby alternatives.",
    keyword: "is EV charger working",
    imageAlt: "VeVolt Community reports for an EV charging station",
    lead: "No single signal is perfect. The safest decision combines official operator information, recent community context and a backup location.",
    sectionHeadings: ["Look for official status first", "Read recent reports", "Always keep a fallback"],
    paragraphs: [
      "When the operator provides a real-time app or support channel, use it to check connector status and access requirements before leaving.",
      "Recent reports can reveal a broken plug, occupied bay, blocked entrance or new tariff. Pay attention to the date and whether the report matches your connector.",
      "For a low-battery arrival, identify another compatible charger before starting the route. A fallback turns uncertainty into a manageable detour.",
    ],
    bullets: ["Operator status", "Timestamp of reports", "Matching connector", "Venue opening hours", "Compatible backup"],
    faqs: [["Does a recent positive report guarantee success?", "No. Conditions can change between the report and your arrival."], ["What should I report after a failed attempt?", "Share the connector, time, visible error and access condition without exposing personal data."]],
  },
  {
    slug: "electric-car-without-home-charger",
    title: "Can you own an electric car without a home charger?",
    description: "Assess public charging, workplace access, routine distance, charging time and cost before relying on an EV without home charging.",
    keyword: "electric car without home charger",
    imageAlt: "VeVolt map and nearby charging services",
    lead: "It can work, but the answer depends more on routine than on the car alone. Reliable nearby charging and predictable weekly mileage are essential.",
    sectionHeadings: ["Map your weekly routine", "Evaluate reliability and time", "Create a charging habit"],
    paragraphs: [
      "Estimate weekly distance and identify chargers near home, work, shopping or regular destinations. A convenient destination charger can be more useful than a fast charger far away.",
      "Check opening hours, queues, tariffs and alternatives. The plan is fragile if it depends on one connector that is often occupied or inaccessible.",
      "Choose one or two routine charging windows and keep an emergency option. Recording sessions helps reveal the real monthly cost and time commitment.",
    ],
    bullets: ["Weekly mileage", "Nearby reliable locations", "Time available while parked", "Total charging cost", "Emergency alternative"],
    faqs: [["Do I need fast charging every time?", "No. Destination or workplace AC charging may cover the routine when the car remains parked long enough."], ["What is the biggest risk?", "Depending on a single unreliable location without a compatible backup."]],
  },
  {
    slug: "electric-car-versus-gasoline-cost-per-kilometer",
    title: "Electric car versus gasoline: how to compare cost per kilometer",
    description: "Compare electricity and fuel fairly using real consumption, energy price, charging losses and the same distance period.",
    keyword: "electric car versus gasoline cost",
    imageAlt: "VeVolt economy dashboard comparing electric charging costs",
    lead: "A fair comparison uses the same distance and real local prices. Comparing a full battery with a full fuel tank hides the different capacity and efficiency of each vehicle.",
    sectionHeadings: ["Calculate the electric cost", "Calculate the fuel cost", "Compare a meaningful period"],
    paragraphs: [
      "Multiply electric consumption in kWh per 100 km by the effective price per kWh, including losses and recurring charging fees when relevant.",
      "Divide the fuel price per liter by the vehicle's real kilometers per liter. Use your own driving pattern rather than only laboratory figures.",
      "Compare a month or several similar trips and remember that energy cost is only one part of total ownership. Maintenance, insurance, depreciation and taxes are separate decisions.",
    ],
    bullets: ["Same distance", "Real electricity price", "Real fuel price", "Observed consumption", "Comparable driving conditions"],
    faqs: [["Is electricity always cheaper per kilometer?", "It often is, but local tariffs, fast-charging use and vehicle efficiency can change the result."], ["Does VeVolt calculate total ownership cost?", "No. Its economy records focus on energy, charging cost and fuel comparison."]],
  },
  {
    slug: "ev-charging-station-map-brazil-2026",
    title: "EV charging-station map in Brazil: how to read the network in 2026",
    description: "Understand public and semi-public charging coverage, connector data, regional gaps and what a map marker really means.",
    keyword: "EV charging station map Brazil 2026",
    imageAlt: "VeVolt map of charging stations in Brazil",
    lead: "Brazil's charging network is expanding, but coverage is not uniform. A useful map must help drivers inspect the details behind each marker.",
    sectionHeadings: ["Read network growth carefully", "Inspect each location", "Plan for regional differences"],
    paragraphs: [
      "National totals combine different powers, connectors and access models. Growth is important, but it does not mean every highway or city has the same practical coverage.",
      "Open the location to check connector, power, address, operator, tariff and access conditions. One map marker may represent more than one charging connection.",
      "Longer trips should account for regional gaps and identify alternatives around each planned stop. Community reports add context but do not replace operator confirmation.",
    ],
    bullets: ["Connector and power", "Number of connections", "Public or restricted access", "Regional alternatives", "Recent condition reports"],
    faqs: [["How many public and semi-public points were reported in May 2026?", "ABVE reported 25,429 points in the survey released in June 2026."], ["Does one marker equal one charger?", "Not necessarily. Open the details to see the available connections and data source."]],
  },
  {
    slug: "how-to-use-ev-charging-at-shopping-centers-hotels",
    title: "EV charging at shopping centers, supermarkets and hotels",
    description: "Check access, parking, session activation, stay limits and etiquette when charging at a destination.",
    keyword: "EV charging at shopping center",
    imageAlt: "VeVolt Market showing nearby businesses and EV services",
    lead: "Destination charging works well because the car charges while you do something else. The experience depends on understanding the venue's rules and leaving the bay on time.",
    sectionHeadings: ["Confirm access and opening hours", "Understand the total cost", "Use the bay responsibly"],
    paragraphs: [
      "Check whether the charger is inside paid parking, a guest-only area or a garage that closes. Some locations require reception approval or an operator app.",
      "Energy can be free while parking is paid, or the opposite. Look for session fees, minimum purchases and maximum-stay rules before connecting.",
      "Start charging promptly, monitor the session and move the vehicle when the target is reached. Report equipment faults to the operator and, when useful, to the community.",
    ],
    bullets: ["Opening hours", "Parking access", "Activation method", "Energy and parking cost", "Maximum stay"],
    faqs: [["Is shopping-center charging always free?", "No. Energy, parking and session fees vary by venue and operator."], ["Can I leave the car after charging ends?", "Follow local rules and free the bay as soon as practical so another driver can use it."]],
  },
];

const es = [
  {
    slug: "punto-de-recarga-coche-electrico-cerca-de-mi", title: "Punto de recarga para coche eléctrico cerca de mí: cómo encontrarlo", description: "Aprende a localizar cargadores cercanos y revisar conector, potencia, acceso, precio y condiciones antes de salir.", keyword: "punto de recarga coche eléctrico cerca de mí", imageAlt: "Mapa de VeVolt con puntos de recarga cercanos", lead: "Un marcador en el mapa es solo el principio. Una parada útil debe encajar con tu ruta, tu vehículo, el tiempo disponible y las condiciones que puedes encontrar al llegar.", sectionHeadings: ["Empieza por tu viaje", "Revisa lo que cambia la decisión", "Usa los reportes con contexto"], paragraphs: ["Comprueba la batería restante, la distancia pendiente y si la parada es urgente o puede coincidir con otra actividad. El punto más cercano no siempre es el más conveniente.", "Abre los detalles y compara conector, potencia, dirección, horario, acceso y tarifa cuando esté disponible. Para una parada crítica, confirma con el operador o establecimiento.", "Los reportes recientes pueden mostrar fallas, filas, acceso bloqueado o cambios de precio. Aportan contexto, pero no son disponibilidad oficial en tiempo real."], bullets: ["Conector compatible", "Potencia útil", "Horario y acceso", "Precio y estacionamiento", "Reportes recientes"], faqs: [["¿VeVolt garantiza que el cargador está disponible?", "No. Organiza datos públicos y reportes; confirma la información crítica con el operador."], ["¿Siempre conviene el punto más cercano?", "No. Compatibilidad, potencia y fiabilidad pueden justificar una parada algo más distante."]],
  },
  {
    slug: "cuanto-cuesta-cargar-un-coche-electrico", title: "¿Cuánto cuesta cargar un coche eléctrico?", description: "Calcula el costo de una recarga con capacidad de batería, precio de energía, pérdidas, estacionamiento y tasas.", keyword: "cuánto cuesta cargar coche eléctrico", imageAlt: "Pantalla de costos y ahorro de VeVolt", lead: "El costo de carga no es una cifra fija. Depende de los kWh recibidos, la tarifa local y los cargos del operador o del estacionamiento.", sectionHeadings: ["Empieza por los kWh", "Incluye el costo completo", "Compara períodos reales"], paragraphs: ["Multiplica la energía añadida por el precio del kWh. Si solo conoces el porcentaje, estima la fracción de la capacidad útil que necesitas recuperar.", "Añade pérdidas de carga y revisa tasas de sesión, inactividad, membresía y estacionamiento. Energía gratuita no siempre significa parada gratuita.", "Registra energía e importe después de cada sesión. Un historial pequeño ofrece un costo por kilómetro más realista que un cálculo aislado."], bullets: ["Energía en kWh", "Precio por kWh", "Pérdidas", "Tasas de sesión", "Estacionamiento"], faqs: [["¿La carga pública siempre es más cara?", "No siempre, aunque la carga rápida suele incluir costos de infraestructura y servicio."], ["¿VeVolt cobra la energía?", "No. El pago corresponde al operador o al establecimiento."]],
  },
  {
    slug: "cuanto-tarda-en-cargarse-un-coche-electrico", title: "¿Cuánto tarda en cargarse un coche eléctrico?", description: "Entiende cómo influyen potencia, límites del vehículo, batería y temperatura en el tiempo de recarga.", keyword: "tiempo de carga coche eléctrico", imageAlt: "Información de recarga de un coche eléctrico en VeVolt", lead: "La potencia indicada por el cargador no es constante ni garantizada. El vehículo, la batería y la curva de carga determinan la velocidad real.", sectionHeadings: ["Haz una primera estimación", "Entiende la curva de carga", "Planifica el tiempo disponible"], paragraphs: ["Divide la energía que necesitas añadir entre la potencia media efectiva. Añade margen para pérdidas y variaciones durante la sesión.", "La carga rápida suele reducir potencia cuando la batería se llena. Temperatura, acondicionamiento y potencia compartida también influyen.", "En una parada corta, busca la autonomía necesaria para el siguiente tramo en lugar de cargar automáticamente al 100%."], bullets: ["Límite AC/DC del vehículo", "Potencia del cargador", "Batería inicial", "Objetivo de carga", "Temperatura"], faqs: [["¿Un cargador de 150 kW entrega siempre 150 kW?", "No. El vehículo y la batería deciden cuánto aceptan en cada momento."], ["¿Por qué baja la potencia cerca del 100%?", "La gestión de batería reduce potencia para protegerla."]],
  },
  {
    slug: "conectores-coche-electrico-tipo-2-ccs2-chademo-gbt", title: "Conectores de coche eléctrico: Tipo 2, CCS2, CHAdeMO y GB/T", description: "Identifica conectores habituales y confirma la compatibilidad antes de elegir un punto de recarga.", keyword: "tipos de conectores coche eléctrico", imageAlt: "Detalles de conector de un punto de recarga en VeVolt", lead: "Un cargador puede estar cerca y ser potente, pero no servir si el conector no coincide con el vehículo. La compatibilidad se revisa antes de iniciar la ruta.", sectionHeadings: ["Diferencia carga AC y DC", "Compara con el vehículo", "Mira más allá de la forma"], paragraphs: ["Tipo 2 se asocia habitualmente a carga AC, mientras CCS2 añade contactos para carga rápida DC. CHAdeMO y GB/T pertenecen a otros ecosistemas.", "El manual y la entrada de carga del vehículo son la referencia. Los adaptadores no son universales y deben estar aprobados.", "Tener el mismo conector no garantiza la potencia máxima. Los límites del coche y la estación siguen determinando el rendimiento."], bullets: ["Estándar del conector", "AC o DC", "Límite del vehículo", "Cable incluido", "Adaptador aprobado"], faqs: [["¿Tipo 2 y CCS2 son iguales?", "CCS2 incorpora la parte Tipo 2 y añade contactos DC."], ["¿Un adaptador sirve para cualquier cargador?", "No. También importan comunicación, tensión y aprobación del fabricante."]],
  },
  {
    slug: "como-planificar-un-viaje-en-coche-electrico", title: "Cómo planificar un viaje en coche eléctrico", description: "Organiza una ruta con autonomía, cargadores compatibles, alternativas, tiempo de carga y margen de llegada.", keyword: "planificar viaje coche eléctrico", imageAlt: "Ruta de VeVolt con batería estimada al llegar", lead: "Un plan fiable combina la autonomía real del vehículo con opciones de carga y deja margen para clima, tráfico, desnivel y cambios en las estaciones.", sectionHeadings: ["Construye la ruta desde el vehículo", "Elige paradas con alternativas", "Actualiza el plan durante el viaje"], paragraphs: ["Empieza con batería actual, autonomía práctica y conector. Usa un margen conservador en lugar de tratar la autonomía máxima como garantía.", "Prefiere zonas con alternativas compatibles, servicios y potencia adecuada. Revisa horario y requisitos del operador antes de salir.", "Después de cada tramo, vuelve a mirar consumo y batería estimada. Viento, lluvia, velocidad, temperatura y desnivel pueden cambiar el plan."], bullets: ["Batería y autonomía real", "Cargador principal", "Alternativa compatible", "Acceso y aplicación", "Reserva al llegar"], faqs: [["¿Cuánta batería conviene dejar al llegar?", "Depende de la ruta y las alternativas; usa una reserva acorde al riesgo del trayecto."], ["¿VeVolt reemplaza la navegación?", "No. Ayuda con decisiones de recarga y abre la ruta en una app de navegación."]],
  },
  {
    slug: "cargador-coche-electrico-en-condominio", title: "Cargador de coche eléctrico en condominio: qué organizar", description: "Revisa capacidad eléctrica, seguridad, aprobación, agenda, consumo y reparto de costos para carga compartida.", keyword: "cargador coche eléctrico condominio", imageAlt: "Gestión de recarga compartida en VeVolt Condo", lead: "La carga en un condominio no es solo una decisión de hardware. Capacidad, seguridad, acceso y reparto transparente deben funcionar juntos.", sectionHeadings: ["Empieza por la evaluación técnica", "Define reglas compartidas", "Mantén registros transparentes"], paragraphs: ["Un profesional debe evaluar demanda, protecciones, recorrido de cables y normas de seguridad aplicables antes de instalar.", "El condominio debe definir quién usa cada cargador, cómo se reservan horarios y cómo se reportan incidentes.", "VeVolt Condo organiza participantes, cargadores, agenda y reparto mensual, pero no controla físicamente el equipo ni cobra la energía."], bullets: ["Capacidad eléctrica", "Normas de instalación", "Acceso de usuarios", "Reservas", "Consumo y reparto"], faqs: [["¿VeVolt Condo controla el cargador?", "No. Organiza personas, horarios y registros."], ["¿El residente invitado paga Condo?", "No. El administrador contrata y los invitados entran gratuitamente."]],
  },
  {
    slug: "carga-rapida-coche-electrico-buenas-practicas", title: "Carga rápida de coche eléctrico: buenas prácticas", description: "Usa la carga rápida con eficiencia revisando compatibilidad, batería, curva de carga, fila y duración de la parada.", keyword: "carga rápida coche eléctrico", imageAlt: "Detalles de un punto de carga rápida en VeVolt", lead: "La carga rápida es más útil cuando acorta el viaje. La decisión debe centrarse en la energía necesaria para el siguiente tramo, no solo en la potencia máxima.", sectionHeadings: ["Llega preparado", "Aprovecha la parte útil de la curva", "Deja información clara"], paragraphs: ["Confirma conector, acceso y método de pago. El preacondicionamiento de batería, cuando existe, puede mejorar el rendimiento.", "La potencia suele bajar con un nivel alto de batería. Llegar al 100% puede tardar más que realizar otra parada bien situada.", "Libera la plaza al alcanzar el objetivo y reporta fallas o problemas de acceso para ayudar al siguiente conductor."], bullets: ["Conector DC", "Cuenta del operador", "Preacondicionamiento", "Objetivo del siguiente tramo", "Tasa de inactividad"], faqs: [["¿Está prohibido usar carga rápida a menudo?", "No, pero sigue las recomendaciones de cuidado de batería del fabricante."], ["¿Por qué cambia la potencia?", "Nivel, temperatura, límites del vehículo y condiciones de la estación afectan la curva."]],
  },
  {
    slug: "electrolinera-gratis-o-de-pago", title: "Electrolinera gratis o de pago: cómo saberlo", description: "Revisa precio de energía, estacionamiento, tasas, acceso y requisitos del operador antes de llegar.", keyword: "electrolinera gratis", imageAlt: "Punto de recarga con información de tarifa en VeVolt", lead: "Un cargador gratuito puede estar dentro de un estacionamiento de pago. Mira siempre el costo completo de la parada.", sectionHeadings: ["Lee la tarifa", "Confirma las condiciones", "Registra el costo real"], paragraphs: ["Comprueba si se cobra por kWh, minuto, sesión o membresía, además de tasas por permanecer conectado tras finalizar.", "Centros comerciales y hoteles pueden limitar el uso a clientes o huéspedes. Horario y estacionamiento importan tanto como la energía.", "Después de cargar, registra energía e importe. Los datos reales mejoran futuras comparaciones."], bullets: ["Precio de energía", "Tasa de sesión", "Tasa de inactividad", "Estacionamiento", "Requisito de cliente"], faqs: [["¿Carga gratis significa estacionamiento gratis?", "No. Son cobros independientes."], ["¿Los precios de la comunidad están garantizados?", "No. Confirma en el operador o el lugar."]],
  },
  {
    slug: "como-saber-si-un-cargador-electrico-funciona", title: "Cómo saber si un cargador eléctrico funciona", description: "Reduce la incertidumbre revisando estado del operador, reportes recientes, acceso y alternativas cercanas.", keyword: "saber si cargador eléctrico funciona", imageAlt: "Reportes de la Comunidad VeVolt sobre un cargador", lead: "Ninguna señal es perfecta. La mejor decisión combina información oficial, contexto comunitario y una alternativa compatible.", sectionHeadings: ["Busca el estado oficial", "Lee reportes recientes", "Guarda una alternativa"], paragraphs: ["Cuando el operador ofrece estado en tiempo real o soporte, úsalo para revisar conectores y requisitos.", "Los reportes pueden mostrar enchufes dañados, plazas ocupadas o acceso bloqueado. Mira fecha y conector.", "Con batería baja, identifica otra opción antes de salir. Una alternativa convierte la incertidumbre en un desvío manejable."], bullets: ["Estado del operador", "Fecha del reporte", "Conector correcto", "Horario", "Alternativa"], faqs: [["¿Un reporte positivo garantiza que funcionará?", "No. La situación puede cambiar antes de llegar."], ["¿Qué debo reportar tras una falla?", "Conector, hora, error visible y acceso, sin exponer datos personales."]],
  },
  {
    slug: "coche-electrico-sin-cargador-en-casa", title: "¿Se puede tener coche eléctrico sin cargador en casa?", description: "Evalúa carga pública, acceso en el trabajo, distancia semanal, tiempo y costo antes de depender de la red externa.", keyword: "coche eléctrico sin cargador en casa", imageAlt: "Mapa de VeVolt con cargadores y servicios cercanos", lead: "Puede funcionar, pero depende más de la rutina que del coche. Necesitas puntos fiables cerca de tus actividades y un kilometraje previsible.", sectionHeadings: ["Mapea tu semana", "Evalúa tiempo y fiabilidad", "Crea un hábito"], paragraphs: ["Calcula la distancia semanal y localiza cargadores cerca de casa, trabajo y destinos habituales.", "Revisa horarios, filas, tarifas y alternativas. El plan es frágil si depende de un único conector.", "Define una o dos ventanas de carga y conserva una opción de emergencia. Registrar sesiones revela el costo y tiempo reales."], bullets: ["Distancia semanal", "Puntos fiables", "Tiempo estacionado", "Costo total", "Alternativa de emergencia"], faqs: [["¿Necesito carga rápida siempre?", "No. La carga AC en destino puede cubrir la rutina si el coche permanece estacionado."], ["¿Cuál es el mayor riesgo?", "Depender de un único punto sin alternativa."]],
  },
  {
    slug: "coche-electrico-versus-gasolina-coste-por-kilometro", title: "Coche eléctrico versus gasolina: costo por kilómetro", description: "Compara energía y combustible con consumo real, precios locales, pérdidas y la misma distancia.", keyword: "coche eléctrico versus gasolina costo", imageAlt: "Panel de economía de VeVolt comparando costos", lead: "Una comparación justa utiliza la misma distancia y precios reales. Comparar una batería llena con un depósito lleno oculta diferencias de capacidad y eficiencia.", sectionHeadings: ["Calcula el costo eléctrico", "Calcula el combustible", "Compara un período útil"], paragraphs: ["Multiplica kWh por 100 km por el precio efectivo del kWh, incluidas pérdidas y tasas recurrentes.", "Divide el precio por litro entre los kilómetros reales por litro. Usa tu conducción, no solo datos de laboratorio.", "Compara un mes o trayectos semejantes. Mantenimiento, seguro, depreciación e impuestos son decisiones separadas."], bullets: ["Misma distancia", "Precio eléctrico real", "Precio de combustible", "Consumo observado", "Condiciones comparables"], faqs: [["¿La electricidad siempre es más barata?", "A menudo sí, pero tarifas, carga rápida y eficiencia pueden cambiar el resultado."], ["¿VeVolt calcula el costo total de propiedad?", "No. Se centra en energía, recargas y comparación con combustible."]],
  },
  {
    slug: "mapa-electrolineras-brasil-2026", title: "Mapa de electrolineras en Brasil: cómo leer la red en 2026", description: "Entiende cobertura, conectores, diferencias regionales y qué representa cada marcador.", keyword: "mapa electrolineras Brasil 2026", imageAlt: "Mapa de VeVolt con puntos de recarga en Brasil", lead: "La red brasileña crece, pero la cobertura no es uniforme. Un mapa útil permite abrir cada marcador y revisar sus detalles.", sectionHeadings: ["Interpreta el crecimiento", "Abre cada punto", "Planifica diferencias regionales"], paragraphs: ["Los totales nacionales combinan distintas potencias, conexiones y modelos de acceso. Crecer no significa cobertura igual en todas las rutas.", "Revisa conector, potencia, dirección, operador, tarifa y acceso. Un marcador puede representar varias conexiones.", "En viajes largos, identifica alternativas alrededor de cada parada. Los reportes añaden contexto, pero no reemplazan la confirmación."], bullets: ["Conector y potencia", "Número de conexiones", "Acceso", "Alternativas", "Reportes recientes"], faqs: [["¿Cuántos puntos había en mayo de 2026?", "ABVE informó 25.429 puntos públicos y semipúblicos."], ["¿Un marcador equivale a un cargador?", "No necesariamente. Abre los detalles."]],
  },
  {
    slug: "recarga-en-centro-comercial-hotel-supermercado", title: "Recarga en centro comercial, supermercado y hotel", description: "Comprueba acceso, estacionamiento, activación, tiempo máximo y etiqueta al cargar en un destino.", keyword: "recarga coche eléctrico centro comercial", imageAlt: "VeVolt Mercado con negocios y servicios cercanos", lead: "La carga en destino es cómoda porque ocurre mientras realizas otra actividad. Funciona mejor cuando conoces las reglas y liberas la plaza a tiempo.", sectionHeadings: ["Confirma acceso y horario", "Entiende el costo total", "Usa la plaza con responsabilidad"], paragraphs: ["Revisa si el cargador está dentro de un estacionamiento de pago, una zona de huéspedes o un garaje que cierra.", "La energía puede ser gratis y el estacionamiento de pago, o al revés. Mira tasas, consumo mínimo y permanencia máxima.", "Inicia la sesión, acompaña su progreso y mueve el vehículo al alcanzar el objetivo. Reporta fallas al operador."], bullets: ["Horario", "Acceso", "Activación", "Costo", "Permanencia máxima"], faqs: [["¿La carga en centros comerciales siempre es gratis?", "No. Depende del establecimiento y operador."], ["¿Puedo dejar el coche después?", "Sigue las reglas y libera la plaza cuanto antes."]],
  },
];

export const articlesByLocale = {
  pt: portugueseArticles,
  en: en.map((entry, index) => materialize(portugueseArticles[index], entry, "en")),
  es: es.map((entry, index) => materialize(portugueseArticles[index], entry, "es")),
};

const sourceLabels = {
  en: [
    ["aneel", "ANEEL - Electric vehicles and charging rules"],
    ["abve", "ABVE - Brazil reached 25,429 charging points in May 2026"],
    ["conectores", "Inmetro - Analysis of electric-vehicle charging connectors"],
    ["abastecimento", "Inmetro - Study of electric-vehicle charging systems"],
    ["consumo", "Inmetro - How electric-vehicle consumption is calculated"],
    ["afdc.energy.gov/fuels", "U.S. Department of Energy - Electric Vehicle Charging Stations"],
    ["afdc.energy.gov/vehicles", "U.S. Department of Energy - Electric Vehicles for Consumers"],
    ["maps", "Google Maps - Built-in features for electric vehicles"],
    ["waze", "Waze - Find charging stations along a route"],
    ["agenciasp", "São Paulo State Government - Charging in condominiums"],
    ["ba.gov.br", "Bahia Fire Department - Safety at EV charging locations"],
  ],
  es: [
    ["aneel", "ANEEL - Vehículos eléctricos y reglas de recarga"],
    ["abve", "ABVE - Brasil alcanzó 25.429 puntos de recarga en mayo de 2026"],
    ["conectores", "Inmetro - Análisis de conectores para vehículos eléctricos"],
    ["abastecimento", "Inmetro - Estudio de sistemas de recarga eléctrica"],
    ["consumo", "Inmetro - Cómo se calcula el consumo de los coches eléctricos"],
    ["afdc.energy.gov/fuels", "Departamento de Energía de EE. UU. - Estaciones de recarga"],
    ["afdc.energy.gov/vehicles", "Departamento de Energía de EE. UU. - Vehículos eléctricos"],
    ["maps", "Google Maps - Funciones para vehículos eléctricos"],
    ["waze", "Waze - Encontrar cargadores en la ruta"],
    ["agenciasp", "Gobierno de São Paulo - Recarga en condominios"],
    ["ba.gov.br", "Bomberos de Bahía - Seguridad en puntos de recarga"],
  ],
};

export function localizedSourceLabel(localeKey, source) {
  if (localeKey === "pt") return source.label;
  const match = sourceLabels[localeKey].find(([fragment]) => source.url.includes(fragment));
  return match?.[1] || source.label;
}

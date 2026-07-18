const sources = {
  aneel: {
    label: "ANEEL - Veículos elétricos e regras para recarga",
    url: "https://www.gov.br/aneel/pt-br/assuntos/veiculos-eletricos",
  },
  abve2026: {
    label: "ABVE - Rede brasileira chega a 25.429 pontos em maio de 2026",
    url: "https://abve.org.br/recarga-rapida-dc-cresce-33-em-tres-meses-e-puxa-a-expansao-da-rede/",
  },
  inmetroConnectors: {
    label: "Inmetro - Análise sobre conectores para veículos elétricos",
    url: "https://www.gov.br/inmetro/pt-br/assuntos/regulamentacao/analise-de-impacto-regulatorio/realizadas/2024/conectores-para-carregamento-de-veiculos-eletricos-plug-in/relatorio",
  },
  inmetroCharging: {
    label: "Inmetro - Estudo sobre sistemas de abastecimento de veículos elétricos",
    url: "https://www.gov.br/inmetro/pt-br/centrais-de-conteudo/noticias/inmetro-abre-consulta-publica-sobre-regulamentacao-de-sistemas-de-abastecimento-de-veiculos-eletricos/relatorio_air_save__3_jul_25_.pdf",
  },
  inmetroConsumption: {
    label: "Inmetro - Como é calculado o consumo dos carros elétricos",
    url: "https://www.gov.br/inmetro/pt-br/acesso-a-informacao/perguntas-frequentes/avaliacao-da-conformidade/etiquetagem-para-veiculos-leves/como-e-calculado-o-consumo-dos-carros-eletricos",
  },
  afdcStations: {
    label: "U.S. Department of Energy - Electric Vehicle Charging Stations",
    url: "https://afdc.energy.gov/fuels/electricity-stations",
  },
  afdcConsumers: {
    label: "U.S. Department of Energy - Electric Vehicles for Consumers",
    url: "https://afdc.energy.gov/vehicles/electric-consumers",
  },
  maps: {
    label: "Google Maps - Recursos integrados para veículos elétricos",
    url: "https://support.google.com/maps/answer/9773205?hl=pt-BR",
  },
  waze: {
    label: "Waze - Encontrar eletropostos na rota",
    url: "https://support.google.com/waze/answer/13259753?hl=pt-BR",
  },
  spCondo: {
    label: "Governo de São Paulo - Lei 18.403/2026 sobre recarga em condomínios",
    url: "https://www.agenciasp.sp.gov.br/governo-de-sp-sanciona-lei-que-permite-instalacao-de-carregadores-de-carros-eletricos-em-condominios/",
  },
  cbmba: {
    label: "Corpo de Bombeiros da Bahia - Segurança em locais com recarga",
    url: "https://www.ba.gov.br/ssp/noticias/2026-05/88443/cbm-homologa-instrucao-tecnica-para-seguranca-em-recarga-de-veiculos",
  },
};

export const articles = [
  {
    slug: "ponto-de-recarga-carro-eletrico-perto-de-mim",
    title: "Ponto de recarga para carro elétrico perto de mim: como encontrar e confirmar",
    description:
      "Aprenda a localizar eletropostos próximos, conferir conector, potência, tarifa e condições antes de sair.",
    keyword: "ponto de recarga carro elétrico perto de mim",
    image: "visuals/pt-feature-map-1600.webp",
    imageAlt: "Mapa do VeVolt mostrando pontos de recarga próximos",
    readTime: 8,
    lead:
      "Encontrar um ponto no mapa é só o começo. Uma boa escolha combina distância, conector compatível, potência, preço informado e sinais recentes de que o local realmente pode atender você.",
    sections: [
      {
        heading: "Comece pela necessidade da sua viagem",
        paragraphs: [
          "Antes de abrir qualquer mapa, responda a três perguntas: quanta bateria resta, quantos quilômetros você ainda precisa rodar e se a parada é urgente ou pode acontecer durante outra atividade. Essa leitura evita escolher o ponto simplesmente porque ele aparece mais perto.",
          "Para uma recarga de oportunidade durante compras ou trabalho, um carregador em corrente alternada pode ser suficiente. Em deslocamentos longos, a potência e o tempo disponível pesam mais. O ponto mais próximo nem sempre é o melhor ponto para o seu momento.",
        ],
      },
      {
        heading: "Confira os dados que mudam a decisão",
        paragraphs: [
          "No VeVolt, abra os detalhes do ponto e observe o tipo de conector, a potência, a distância, o endereço e a tarifa quando ela tiver sido informada. Compare essas informações com o manual e a entrada de recarga do seu veículo.",
          "Também vale olhar as condições de acesso. Alguns carregadores ficam em estacionamentos com horário limitado, exigem cadastro no aplicativo do operador ou estão em áreas destinadas a clientes. Uma checagem de dois minutos pode poupar um desvio frustrante.",
        ],
        bullets: [
          "Conector compatível com o veículo",
          "Potência adequada ao tempo disponível",
          "Tarifa e eventual cobrança de estacionamento",
          "Horário, acesso e regras do local",
          "Relatos recentes da comunidade",
        ],
      },
      {
        heading: "Use relatos como contexto, não como garantia",
        paragraphs: [
          "Uma confirmação recente ajuda a entender o que outro motorista encontrou: equipamento funcionando, fila, defeito, preço diferente ou acesso bloqueado. Ainda assim, a situação pode mudar entre o relato e a sua chegada.",
          "Por isso o VeVolt diferencia informação comunitária de disponibilidade oficial. Para uma parada crítica, confirme também no aplicativo do operador ou diretamente com o estabelecimento. A melhor rota é aquela que inclui uma alternativa possível.",
        ],
      },
      {
        heading: "Como o VeVolt reduz a incerteza",
        paragraphs: [
          "Salve os pontos relevantes, acompanhe a comunidade e mantenha os dados do seu veículo atualizados. Assim, mapa, rota e compatibilidade trabalham juntos. Depois da visita, registre o que encontrou: uma informação curta e objetiva pode ser decisiva para a próxima pessoa.",
        ],
      },
    ],
    faqs: [
      ["O ponto mais próximo sempre é a melhor opção?", "Não. Conector, potência, acesso, preço e confiabilidade podem tornar outro ponto mais adequado."],
      ["O VeVolt garante que o carregador está disponível?", "Não. O app reúne dados públicos e comunitários; condições críticas devem ser confirmadas com o operador ou no local."],
    ],
    sources: [sources.abve2026, sources.maps, sources.waze],
  },
  {
    slug: "quanto-custa-carregar-carro-eletrico",
    title: "Quanto custa carregar um carro elétrico? Faça a conta por kWh",
    description:
      "Entenda como calcular o custo de uma recarga em casa ou em eletropostos e comparar o valor por quilômetro.",
    keyword: "quanto custa carregar carro elétrico",
    image: "screens/pt-economy-720.webp",
    imageAlt: "Tela de economia do VeVolt com energia e custo de recarga",
    readTime: 9,
    lead:
      "O custo não depende apenas do tamanho da bateria. Você precisa considerar quanta energia entrou, o preço do kWh, eventuais taxas do local e as perdas naturais do processo de recarga.",
    sections: [
      {
        heading: "A fórmula básica da recarga",
        paragraphs: [
          "A conta mais simples é energia adicionada em kWh multiplicada pelo preço do kWh. Se você adicionou 30 kWh e pagou R$ 1,50 por kWh, o valor energético foi R$ 45. Em casa, use a tarifa efetiva da sua conta; em um eletroposto, consulte o preço apresentado pelo operador.",
          "Uma carga de 20% a 80% não adiciona a capacidade total da bateria. Para estimar, multiplique a capacidade útil pela diferença percentual. Em uma bateria de 50 kWh, subir 60 pontos percentuais representa aproximadamente 30 kWh antes de considerar perdas.",
        ],
      },
      {
        heading: "O que pode entrar no preço final",
        bullets: [
          "Energia cobrada por kWh",
          "Taxa por minuto, sessão ou ocupação após o término",
          "Estacionamento, pedágio ou acesso ao estabelecimento",
          "Variação de tarifa por horário ou plano do operador",
          "Perdas entre a rede, o carregador e a bateria",
        ],
        paragraphs: [
          "A ANEEL permite a exploração comercial da recarga com preços livremente negociados. Isso significa que dois pontos próximos podem adotar modelos de cobrança diferentes. Leia a tela de confirmação do operador antes de iniciar.",
        ],
      },
      {
        heading: "Compare custo por quilômetro, não só carga completa",
        paragraphs: [
          "Para comparar com gasolina ou etanol, registre a energia e a distância percorrida. Divida o valor pago pelos quilômetros efetivamente rodados naquele período. Essa métrica conversa melhor com sua rotina do que o preço para encher a bateria.",
          "O consumo varia com velocidade, temperatura, relevo, pneus, climatização e estilo de condução. Use médias de várias recargas para evitar conclusões baseadas em um único trajeto.",
        ],
      },
      {
        heading: "Registre no momento da recarga",
        paragraphs: [
          "No VeVolt, informe a energia e o valor pago enquanto os números ainda estão visíveis. O histórico local ajuda a acompanhar médias e comparar períodos. Seus registros pessoais permanecem no aparelho.",
        ],
      },
    ],
    faqs: [
      ["Quanto custa uma carga completa?", "Multiplique a energia necessária pelo preço do kWh e considere taxas e perdas. O valor varia por bateria, tarifa e local."],
      ["Carregador público pode cobrar qualquer valor?", "A atividade comercial é permitida com preços livremente negociados. O operador deve apresentar as condições da sessão."],
    ],
    sources: [sources.aneel, sources.inmetroConsumption],
  },
  {
    slug: "quanto-tempo-demora-carregar-carro-eletrico",
    title: "Quanto tempo demora para carregar um carro elétrico?",
    description:
      "Veja por que potência, capacidade da bateria, limite do veículo e nível inicial mudam o tempo de recarga.",
    keyword: "quanto tempo demora carregar carro elétrico",
    image: "screens/pt-point-720.webp",
    imageAlt: "Detalhes de um ponto de recarga exibidos no VeVolt",
    readTime: 8,
    lead:
      "A resposta pode variar de menos de uma hora a uma noite inteira. O número escrito no carregador é importante, mas não trabalha sozinho: veículo, bateria e curva de carga também definem o ritmo.",
    sections: [
      {
        heading: "Potência do ponto e limite do veículo",
        paragraphs: [
          "Em corrente alternada, o carregador interno do carro converte a energia e impõe um limite. Um ponto de 22 kW não fará o veículo receber 22 kW se o carregador de bordo aceitar menos. Em corrente contínua, o equipamento envia energia diretamente ao sistema da bateria, mas o carro ainda determina quanto consegue receber.",
          "O Inmetro descreve carregadores domésticos e CA normalmente associados a recargas em horas, enquanto sistemas CC são projetados para recarga rápida. A potência disponível no local pode ainda ser compartilhada entre conectores.",
        ],
      },
      {
        heading: "Uma estimativa simples",
        paragraphs: [
          "Divida a energia que deseja adicionar pela potência efetivamente recebida. Para adicionar 30 kWh a 7 kW, a conta idealizada resulta em pouco mais de quatro horas. Na prática, acrescente margem para perdas e variações de potência.",
          "Em recarga rápida, a potência não costuma permanecer máxima até 100%. O sistema reduz o ritmo em partes da curva para administrar temperatura e bateria. Por isso, estimativas até 80% costumam ser mais úteis em viagens.",
        ],
      },
      {
        heading: "O que altera o tempo real",
        bullets: [
          "Estado de carga inicial e meta desejada",
          "Capacidade útil da bateria",
          "Potência aceita pelo veículo em CA e CC",
          "Temperatura e gerenciamento térmico",
          "Potência compartilhada e condições do eletroposto",
        ],
      },
      {
        heading: "Escolha a parada pelo tempo que você tem",
        paragraphs: [
          "Uma parada longa para refeição pode combinar com uma recarga moderada. Em viagem, um ponto rápido e compatível pode reduzir o tempo total mesmo que fique alguns quilômetros adiante. Consulte potência e conector no VeVolt e confirme as condições no operador.",
        ],
      },
    ],
    faqs: [
      ["Um carregador de 60 kW sempre entrega 60 kW?", "Não. O veículo, a bateria, a temperatura, o compartilhamento e a curva de carga podem reduzir a potência recebida."],
      ["É mais rápido carregar até 100%?", "A parte final pode ocorrer em potência menor. Em viagens, muitos motoristas planejam a parada até o nível necessário para seguir com margem."],
    ],
    sources: [sources.inmetroCharging, sources.afdcStations],
  },
  {
    slug: "tipos-conectores-carro-eletrico-tipo-2-ccs2",
    title: "Tipos de conectores para carro elétrico: Tipo 2, CCS2, CHAdeMO e GB/T",
    description:
      "Entenda a diferença entre conectores de recarga e saiba como verificar a compatibilidade do seu veículo.",
    keyword: "tipos de conector carro elétrico",
    image: "screens/pt-point-720.webp",
    imageAlt: "Tela do VeVolt com informação de conector do eletroposto",
    readTime: 9,
    lead:
      "O formato do plugue precisa combinar com a entrada do carro e com o tipo de corrente usado. Saber o nome do conector evita chegar a um ponto potente que não consegue atender o seu veículo.",
    sections: [
      {
        heading: "Tipo 2 e CCS2 são relacionados, mas não iguais",
        paragraphs: [
          "O Tipo 2 é amplamente usado para recarga em corrente alternada. O CCS2 acrescenta contatos de corrente contínua ao conjunto e é comum em recarga rápida. O Inmetro identificou Tipo 2/CCS2 como padrão predominante no Brasil em sua análise regulatória de conectores.",
          "Um veículo com entrada CCS2 normalmente aceita o plugue Tipo 2 na parte superior para CA, mas a potência e os modos suportados devem ser confirmados no manual.",
        ],
      },
      {
        heading: "Outros formatos que você pode encontrar",
        paragraphs: [
          "CHAdeMO aparece em alguns modelos e redes, especialmente em veículos de gerações anteriores. GB/T é um conjunto de padrões presente em determinados veículos de origem chinesa. Também existem Tipo 1/J1772 e outros formatos regionais.",
          "Adaptadores não devem ser tratados como solução universal. Corrente, comunicação, travamento e segurança fazem parte da compatibilidade. Siga as orientações do fabricante do veículo e do equipamento.",
        ],
      },
      {
        heading: "Conector não é a mesma coisa que potência",
        paragraphs: [
          "Dois pontos CCS2 podem oferecer potências muito diferentes. Da mesma forma, um ponto Tipo 2 pode ser limitado pela rede local ou pelo carregador interno do veículo. Confira sempre os dois campos: conector e kW.",
        ],
      },
      {
        heading: "Cadastre o veículo para filtrar melhor",
        paragraphs: [
          "Mantenha o tipo de conector do seu carro atualizado no VeVolt. O Premium usa compatibilidade nas recomendações e a tela de detalhes deixa a informação visível antes de você traçar a rota.",
        ],
      },
    ],
    faqs: [
      ["CCS2 e Tipo 2 usam a mesma entrada?", "O CCS2 combina a parte Tipo 2 com contatos adicionais para corrente contínua. A compatibilidade exata depende do veículo."],
      ["Posso usar qualquer adaptador?", "Não presuma compatibilidade. Consulte fabricante, manual e especificações de segurança do equipamento."],
    ],
    sources: [sources.inmetroConnectors, sources.afdcStations],
  },
  {
    slug: "como-planejar-viagem-carro-eletrico",
    title: "Como planejar uma viagem de carro elétrico sem ansiedade de autonomia",
    description:
      "Monte uma rota com margem de bateria, paradas alternativas e informações atualizadas sobre eletropostos.",
    keyword: "viajar com carro elétrico",
    image: "screens/pt-route-720.webp",
    imageAlt: "Tela de rota e autonomia do aplicativo VeVolt",
    readTime: 10,
    lead:
      "Viajar de elétrico não exige prever cada minuto, mas pede uma leitura melhor de autonomia, terreno, clima, velocidade e qualidade das paradas. Uma rota resiliente é mais importante que uma rota perfeita no papel.",
    sections: [
      {
        heading: "Planeje a primeira parada antes de sair",
        paragraphs: [
          "Parta com o nível de bateria adequado e escolha a primeira parada com margem. Evite construir um trajeto que só funciona se o consumo repetir exatamente a estimativa. Vento, chuva, relevo, velocidade e climatização podem mudar o resultado.",
          "O Departamento de Energia dos Estados Unidos recomenda identificar previamente as paradas em viagens longas. No Brasil, a expansão da rede ajuda, mas cobertura, potência e qualidade ainda variam entre regiões.",
        ],
      },
      {
        heading: "Tenha uma alternativa próxima",
        paragraphs: [
          "Para cada parada crítica, observe pelo menos uma opção alternativa que o seu carro consiga alcançar. A Rota Segura do VeVolt Premium considera compatibilidade, confiança e até duas alternativas próximas, mas a decisão final continua sendo do motorista.",
          "Não confunda distância em linha reta com trajeto rodoviário. Abra a rota no aplicativo de navegação e confirme o caminho, trânsito e acesso ao estabelecimento.",
        ],
      },
      {
        heading: "Leia o ponto, não apenas o marcador",
        bullets: [
          "Conector e potência compatíveis",
          "Relatos recentes e confiabilidade",
          "Horário de funcionamento",
          "Tarifa e forma de ativação",
          "Serviços úteis durante a parada",
        ],
      },
      {
        heading: "Recalcule durante a viagem",
        paragraphs: [
          "Compare a bateria real com a estimativa após cada trecho. Se a margem caiu mais do que o esperado, antecipe a próxima parada ou reduza a exigência do trajeto. Planejamento bom é aquele que se adapta ao que o carro está mostrando.",
        ],
      },
    ],
    faqs: [
      ["Quanta bateria devo deixar como margem?", "Não existe um número universal. Considere distância até alternativas, clima, relevo, velocidade e experiência com o veículo."],
      ["O VeVolt substitui o aplicativo de navegação?", "Não. Ele ajuda a estimar e escolher pontos; o trajeto final deve ser confirmado no app de navegação."],
    ],
    sources: [sources.afdcConsumers, sources.waze, sources.abve2026],
  },
  {
    slug: "carregador-carro-eletrico-condominio",
    title: "Carregador de carro elétrico em condomínio: instalação, agenda e rateio",
    description:
      "Entenda os pontos técnicos e organizacionais para recarga compartilhada em condomínios.",
    keyword: "carregador carro elétrico condomínio",
    image: "screens/pt-condo-720.webp",
    imageAlt: "Tela do VeVolt Condo com participantes, carregadores e planos",
    readTime: 11,
    lead:
      "A conversa precisa unir segurança elétrica, responsabilidade técnica, regras claras e medição compreensível. O aplicativo organiza o uso; o projeto e a instalação pertencem a profissionais habilitados.",
    sections: [
      {
        heading: "Comece pela capacidade e pela segurança",
        paragraphs: [
          "Antes de escolher wallbox, cabo ou sistema de rateio, o condomínio deve entender a capacidade da instalação elétrica, a demanda simultânea e as exigências locais de segurança. Procure profissional habilitado e observe as regras da distribuidora, da ABNT e do Corpo de Bombeiros do estado.",
          "Requisitos podem variar por localidade e pelo tipo de edificação. Em São Paulo, por exemplo, a Lei estadual 18.403/2026 exige compatibilidade elétrica, execução por profissional habilitado e comunicação prévia à administração para instalações individuais abrangidas pela norma.",
        ],
      },
      {
        heading: "Defina regras antes da primeira reserva",
        bullets: [
          "Quem pode usar cada carregador",
          "Duração máxima e tolerância de horário",
          "Como registrar energia e ocorrências",
          "Critério de rateio e data de fechamento",
          "Responsabilidade por cabo, vaga e liberação após uso",
        ],
        paragraphs: [
          "Regras simples e visíveis reduzem conflitos. A agenda deve refletir a capacidade real, não uma disponibilidade teórica que ignora manutenção ou restrições de potência.",
        ],
      },
      {
        heading: "O papel do VeVolt Condo",
        paragraphs: [
          "O administrador cria uma central privada, cadastra até 10 carregadores e convida até 50 participantes. Moradores entram gratuitamente pelo código, consultam horários e registram manualmente o consumo. O fechamento mensal organiza o rateio por unidade.",
          "O VeVolt Condo não aciona fisicamente o carregador, não mede energia diretamente e não cobra o consumo. Esses limites precisam estar claros para que a ferramenta seja usada como organização, não como sistema elétrico ou meio de pagamento.",
        ],
      },
      {
        heading: "Documente as decisões",
        paragraphs: [
          "Registre projeto, responsabilidade técnica, regras aprovadas, manutenção e alterações. Uma governança simples protege moradores e administração quando a demanda crescer.",
        ],
      },
    ],
    faqs: [
      ["O VeVolt instala ou controla o carregador?", "Não. O Condo organiza participantes, agenda, consumo manual e rateio. Projeto e controle físico são externos ao app."],
      ["A mesma regra vale em todo o Brasil?", "Não necessariamente. Consulte legislação, distribuidora, normas técnicas e Corpo de Bombeiros da sua localidade."],
    ],
    sources: [sources.spCondo, sources.cbmba, sources.aneel],
  },
  {
    slug: "recarga-rapida-carro-eletrico-boas-praticas",
    title: "Recarga rápida no carro elétrico: quando usar e o que observar",
    description:
      "Entenda potência, curva de carga, temperatura e boas práticas para usar carregadores rápidos.",
    keyword: "recarga rápida carro elétrico",
    image: "screens/pt-route-720.webp",
    imageAlt: "Planejamento de rota para carregador no VeVolt",
    readTime: 8,
    lead:
      "A recarga rápida é valiosa quando tempo e distância importam. Para usá-la bem, observe o limite do veículo, o nível de bateria, a potência efetiva e as recomendações do fabricante.",
    sections: [
      {
        heading: "Potência anunciada não é potência constante",
        paragraphs: [
          "Um carregador pode ser classificado em 60, 120 ou 180 kW, mas o carro recebe energia conforme seus próprios limites e a curva de carga. Estado da bateria, temperatura e compartilhamento do equipamento alteram a entrega.",
          "O Inmetro descreve sistemas CC como equipamentos projetados para recarga rápida e destaca faixas de potência variadas. Use a especificação como referência, não como promessa de velocidade durante toda a sessão.",
        ],
      },
      {
        heading: "Pare pelo necessário, não por hábito",
        paragraphs: [
          "Em viagem, a estratégia mais eficiente costuma ser adicionar energia suficiente para chegar à próxima parada com margem. A parte final da carga pode ocorrer mais devagar, então esperar 100% nem sempre reduz o tempo total do trajeto.",
          "Na rotina, escolha o método que se encaixa no período em que o carro ficará parado. Recarga mais lenta pode atender perfeitamente uma noite, um expediente ou uma permanência longa.",
        ],
      },
      {
        heading: "Siga o manual do seu veículo",
        paragraphs: [
          "Baterias e sistemas térmicos variam entre modelos. O manual informa limites, preparação da bateria, recomendações de frequência e condições de segurança. Evite transformar uma regra genérica da internet em orientação universal.",
        ],
      },
      {
        heading: "Antes de traçar a rota",
        paragraphs: [
          "Confira no VeVolt o conector, a potência cadastrada e relatos recentes. Depois valide ativação e preço no operador. Se a parada for essencial, guarde uma alternativa alcançável.",
        ],
      },
    ],
    faqs: [
      ["Recarga rápida sempre entrega a potência máxima?", "Não. Veículo, bateria, temperatura, curva de carga e compartilhamento podem limitar a potência."],
      ["Carregar até 100% é sempre melhor em viagem?", "Não necessariamente. A parte final pode ser mais lenta; carregue o necessário para seguir com margem e respeite o manual."],
    ],
    sources: [sources.inmetroCharging, sources.afdcStations],
  },
  {
    slug: "eletroposto-gratis-ou-pago-como-saber",
    title: "Eletroposto grátis ou pago: como saber antes de carregar",
    description:
      "Veja onde conferir tarifa, taxas, estacionamento e regras de acesso antes de iniciar a recarga.",
    keyword: "ponto de recarga carro elétrico gratuito",
    image: "screens/pt-point-720.webp",
    imageAlt: "Detalhes de preço e condições de um eletroposto no VeVolt",
    readTime: 7,
    lead:
      "A palavra grátis pode significar energia sem cobrança, benefício para clientes ou apenas ausência de tarifa cadastrada. Confirme as condições para não descobrir o custo depois de estacionar.",
    sections: [
      {
        heading: "Tarifa não informada não significa recarga gratuita",
        paragraphs: [
          "Bases públicas podem não ter o preço atualizado. Quando o VeVolt mostra tarifa não informada, trate o campo como desconhecido. Abra o aplicativo do operador, leia a sinalização ou pergunte ao estabelecimento.",
          "Mesmo quando a energia é gratuita, o estacionamento, a permanência ou o acesso ao local podem ser cobrados. Alguns benefícios dependem de cadastro, consumo mínimo ou horário.",
        ],
      },
      {
        heading: "Entenda o modelo de cobrança",
        bullets: [
          "Preço por kWh consumido",
          "Cobrança por minuto conectado",
          "Taxa fixa por sessão",
          "Tarifa de ociosidade depois do término",
          "Estacionamento ou acesso ao estabelecimento",
        ],
        paragraphs: [
          "A ANEEL permite a recarga comercial com preços livremente negociados. Compare o custo total e não apenas um número isolado na tela.",
        ],
      },
      {
        heading: "Relate preços com data e contexto",
        paragraphs: [
          "Se você encontrou um valor diferente, compartilhe na Comunidade VeVolt e deixe claro quando ocorreu e qual condição foi aplicada. Informação contextualizada é mais útil que marcar simplesmente grátis ou pago.",
        ],
      },
      {
        heading: "Registre o que realmente pagou",
        paragraphs: [
          "Ao final, anote energia e valor no VeVolt. O histórico mostra o efeito da tarifa na sua média e facilita comparar pontos ao longo do tempo.",
        ],
      },
    ],
    faqs: [
      ["Tarifa não informada quer dizer grátis?", "Não. Significa apenas que o preço não está disponível naquela fonte. Confirme com o operador ou local."],
      ["Pode existir cobrança além da energia?", "Sim. Estacionamento, sessão, tempo ou ociosidade podem fazer parte do valor final."],
    ],
    sources: [sources.aneel],
  },
  {
    slug: "como-saber-eletroposto-funcionando",
    title: "Como saber se um eletroposto está funcionando antes de sair",
    description:
      "Combine dados do ponto, relatos recentes, horário e confirmação do operador antes de depender de um carregador.",
    keyword: "eletroposto funcionando",
    image: "screens/pt-community-720.webp",
    imageAlt: "Comunidade VeVolt com relatos e confiabilidade do ponto",
    readTime: 8,
    lead:
      "Nenhuma fonte isolada elimina toda incerteza. A decisão fica melhor quando você combina cadastro técnico, relatos recentes, regras de acesso e uma alternativa alcançável.",
    sections: [
      {
        heading: "Verifique a atualização, não só o status",
        paragraphs: [
          "Um marcador operacional pode ter sido registrado há semanas. Dê mais peso a informações recentes e que explicam o contexto: qual conector foi usado, em que horário, se houve fila e se o local estava acessível.",
          "No VeVolt, a Comunidade reúne confirmações, comentários, fotos e nota de confiabilidade. A nota ajuda a ler o histórico, mas não substitui a situação atual.",
        ],
      },
      {
        heading: "Faça uma checagem em camadas",
        bullets: [
          "Dados técnicos do ponto",
          "Últimos relatos da comunidade",
          "Aplicativo ou canal do operador",
          "Horário e regras do estabelecimento",
          "Ponto alternativo compatível",
        ],
      },
      {
        heading: "Sinais de alerta",
        paragraphs: [
          "Relatos repetidos de falha, fotos de equipamento danificado, acesso bloqueado e informações contraditórias merecem cautela. Se o ponto é essencial para chegar ao destino, escolha uma margem maior ou outra parada.",
          "Também observe se o conector relatado é o mesmo que você pretende usar. Um equipamento pode ter mais de uma saída e apenas parte delas estar indisponível.",
        ],
      },
      {
        heading: "Depois da visita, feche o ciclo",
        paragraphs: [
          "Confirme a situação encontrada com texto curto, data implícita e informação objetiva. Evite expor pessoas, placas ou dados desnecessários nas fotos. Uma boa comunidade cresce pela qualidade, não pelo volume.",
        ],
      },
    ],
    faqs: [
      ["Relato recente garante funcionamento?", "Não. Ele aumenta o contexto, mas a condição pode mudar. Confirme no operador e mantenha alternativa."],
      ["Posso denunciar informação inadequada?", "Sim. O VeVolt oferece denúncia, bloqueio de usuário e exclusão do próprio relato."],
    ],
    sources: [sources.maps, sources.waze],
  },
  {
    slug: "carro-eletrico-sem-carregador-em-casa",
    title: "Carro elétrico sem carregador em casa: dá para depender de recarga pública?",
    description:
      "Avalie distância, rotina, frequência, custo e alternativas antes de depender apenas de eletropostos públicos.",
    keyword: "carro elétrico sem carregador em casa",
    image: "visuals/pt-feature-market-1600.webp",
    imageAlt: "VeVolt mostrando mapa e serviços próximos para mobilidade elétrica",
    readTime: 10,
    lead:
      "Pode funcionar muito bem para algumas rotinas e ser cansativo para outras. A resposta depende menos da média da cidade e mais dos lugares onde você realmente passa tempo.",
    sections: [
      {
        heading: "Mapeie sua semana antes de comprar",
        paragraphs: [
          "Liste casa, trabalho, academia, mercado, shopping e trajetos frequentes. Procure carregadores nesses pontos e não apenas no bairro. Uma recarga integrada a uma atividade recorrente pesa menos do que uma parada criada exclusivamente para carregar.",
          "Observe também a distância até alternativas. Uma cidade pode ter muitos pontos no total, mas poucos compatíveis e convenientes para o seu caminho.",
        ],
      },
      {
        heading: "Calcule frequência e folga",
        paragraphs: [
          "Use sua quilometragem semanal e a autonomia realista do veículo. Se a rotina exige uma recarga pública a cada vários dias, a adaptação pode ser simples. Se exige desvios diários, fila e horários rígidos, o custo de conveniência cresce.",
          "Considere semanas atípicas, feriados e viagens. Ter uma opção no trabalho e outra perto de casa é mais resiliente do que depender de um único equipamento.",
        ],
      },
      {
        heading: "Compare custo total e tempo",
        bullets: [
          "Energia e taxas da rede",
          "Estacionamento e consumo mínimo",
          "Tempo conectado e deslocamento adicional",
          "Frequência de filas ou indisponibilidade",
          "Possibilidade de recarga no trabalho ou em destinos recorrentes",
        ],
      },
      {
        heading: "Use dados próprios por algumas semanas",
        paragraphs: [
          "Registre no VeVolt cada sessão e compare energia, valor e local. O histórico mostra se a estratégia pública está funcionando para você. Antes da compra, faça a mesma análise com mapas e visitas aos pontos que pretende usar.",
        ],
      },
    ],
    faqs: [
      ["Preciso carregar o carro elétrico todos os dias?", "Não necessariamente. A frequência depende de autonomia, quilometragem, consumo e margem desejada."],
      ["Uma cidade com muitos pontos resolve o problema?", "Ajuda, mas conveniência, compatibilidade, potência, preço e confiabilidade nos seus trajetos são mais importantes que o total bruto."],
    ],
    sources: [sources.afdcConsumers, sources.abve2026],
  },
  {
    slug: "economia-carro-eletrico-versus-gasolina",
    title: "Carro elétrico versus gasolina: como comparar o custo por quilômetro",
    description:
      "Aprenda a comparar energia e combustível usando registros reais da sua rotina, sem depender de uma conta genérica.",
    keyword: "economia carro elétrico gasolina",
    image: "visuals/pt-feature-premium-1600.webp",
    imageAlt: "Painel Premium e economia do aplicativo VeVolt",
    readTime: 9,
    lead:
      "A comparação mais honesta usa o preço que você realmente paga e a distância que realmente percorre. Tarifa doméstica, eletroposto, consumo, clima e estilo de condução mudam a resposta.",
    sections: [
      {
        heading: "Transforme tudo em custo por quilômetro",
        paragraphs: [
          "No elétrico, divida o valor total das recargas pelos quilômetros do período. No carro a combustão, faça o mesmo com o valor abastecido. Use períodos longos o suficiente para reduzir o efeito de uma viagem, de uma tarifa promocional ou de uma semana atípica.",
          "Se preferir uma estimativa, multiplique o consumo em kWh por 100 km pela tarifa e divida por 100. Para combustíveis, divida o preço por litro pelo rendimento em km/l.",
        ],
      },
      {
        heading: "Separe casa e recarga pública",
        paragraphs: [
          "Misturar todas as sessões sem contexto esconde onde está a economia. Registre o local e o valor. Uma rotina com recarga residencial pode ter perfil diferente de outra baseada em equipamentos rápidos pagos.",
          "Inclua estacionamento ou taxas quando eles foram necessários para carregar. O objetivo é entender sua despesa de mobilidade, não produzir uma comparação artificialmente favorável.",
        ],
      },
      {
        heading: "Use médias e compare períodos equivalentes",
        bullets: [
          "Mesmo intervalo de datas",
          "Quilometragem efetivamente rodada",
          "Custos acessórios relacionados à recarga",
          "Uso urbano e rodoviário semelhante",
          "Variações sazonais de tarifa e consumo",
        ],
      },
      {
        heading: "Acompanhe no seu aparelho",
        paragraphs: [
          "O VeVolt mantém os registros pessoais localmente e permite acompanhar energia, custo e comparações. No Premium, indicadores e histórico detalhado ajudam a enxergar tendências sem enviar seu diário de recargas para publicidade.",
        ],
      },
    ],
    faqs: [
      ["Existe um valor fixo de economia do carro elétrico?", "Não. Tarifa, consumo, uso, combustível comparado e custos acessórios variam por pessoa e período."],
      ["Devo incluir estacionamento?", "Inclua quando ele foi um custo necessário daquela recarga; assim a comparação reflete sua rotina real."],
    ],
    sources: [sources.inmetroConsumption, sources.aneel],
  },
  {
    slug: "mapa-eletropostos-brasil-2026",
    title: "Mapa de eletropostos no Brasil: o que observar em 2026",
    description:
      "Veja como a rede pública cresceu e por que quantidade, potência, localização e confiabilidade precisam ser analisadas juntas.",
    keyword: "mapa de eletropostos no Brasil",
    image: "visuals/pt-feature-map-1600.webp",
    imageAlt: "Composição do mapa de eletropostos no aplicativo VeVolt",
    readTime: 8,
    lead:
      "A rede brasileira cresce rápido, mas o número total não conta toda a história. Para o motorista, importam cobertura no trajeto, potência, compatibilidade, acesso e qualidade da informação.",
    sections: [
      {
        heading: "O retrato nacional está mudando",
        paragraphs: [
          "Levantamento divulgado pela ABVE com dados consolidados até maio de 2026 apontou 25.429 pontos públicos e semipúblicos de recarga no Brasil, crescimento de 20,7% em três meses. A recarga rápida em corrente contínua avançou com força nesse período.",
          "O dado mostra expansão estrutural, mas não significa distribuição uniforme. Capitais, corredores rodoviários e regiões com maior frota podem concentrar mais opções.",
        ],
      },
      {
        heading: "Como ler um mapa de recarga",
        bullets: [
          "Agrupamentos de pontos não são o mesmo que conectores simultâneos",
          "Potência define o uso mais provável da parada",
          "Conector precisa ser compatível",
          "Horário e acesso podem limitar a disponibilidade",
          "Relatos recentes ajudam a qualificar o cadastro",
        ],
        paragraphs: [
          "A ANEEL define o número de pontos de uma estação pela quantidade máxima de veículos que podem ser conectados e carregados ao mesmo tempo. Um local no mapa pode reunir várias saídas ou apenas uma.",
        ],
      },
      {
        heading: "Dados públicos precisam de colaboração",
        paragraphs: [
          "Cadastros mudam: equipamentos entram em manutenção, tarifas são atualizadas e regras de acesso variam. O VeVolt combina fontes abertas com relatos da comunidade para acrescentar contexto, mantendo claro que relatos não equivalem a status oficial.",
        ],
      },
      {
        heading: "Do panorama nacional para a sua rota",
        paragraphs: [
          "Use o crescimento da rede como sinal positivo, mas planeje com os pontos do seu caminho. Cadastre o veículo, filtre compatibilidade, confira alternativas e atualize a rota durante a viagem.",
        ],
      },
    ],
    faqs: [
      ["Quantos pontos públicos e semipúblicos havia em maio de 2026?", "A ABVE informou 25.429 pontos no levantamento divulgado em junho de 2026."],
      ["Um marcador no mapa representa quantos carregadores?", "Depende da fonte e do local. Consulte os detalhes e o número de conexões simultâneas disponíveis."],
    ],
    sources: [sources.abve2026, sources.aneel],
  },
  {
    slug: "como-usar-eletroposto-shopping-mercado-hotel",
    title: "Recarga em shopping, mercado e hotel: o que conferir no local",
    description:
      "Organize acesso, estacionamento, tempo de permanência, ativação e etiqueta ao carregar em estabelecimentos.",
    keyword: "carregar carro elétrico no shopping",
    image: "screens/pt-market-720.webp",
    imageAlt: "VeVolt Mercado com estabelecimentos e serviços próximos",
    readTime: 7,
    lead:
      "Carregadores em destinos são convenientes porque a recarga acontece enquanto você faz outra atividade. A experiência melhora quando você entende as regras do estabelecimento e libera a vaga no momento certo.",
    sections: [
      {
        heading: "Confirme acesso e horário",
        paragraphs: [
          "Verifique se o equipamento fica dentro de estacionamento pago, área exclusiva de hóspedes ou zona com horário de fechamento. Alguns pontos exigem liberação na recepção, cadastro em aplicativo ou retirada de cartão.",
          "Se a parada acontecer fora do horário comercial, confirme se a garagem e o portão continuam acessíveis. Um carregador 24 horas dentro de uma área fechada pode não ser utilizável.",
        ],
      },
      {
        heading: "Entenda o custo total",
        paragraphs: [
          "A energia pode ser gratuita e o estacionamento pago, ou o contrário. Também pode haver consumo mínimo, taxa de sessão ou limite de permanência. Consulte a sinalização e confirme antes de conectar.",
        ],
      },
      {
        heading: "Ocupe a vaga pelo tempo necessário",
        bullets: [
          "Não estacione sem iniciar a recarga",
          "Acompanhe o término da sessão",
          "Retire o veículo quando atingir a meta",
          "Não bloqueie cabos ou vagas adjacentes",
          "Relate defeitos ao operador e à comunidade",
        ],
      },
      {
        heading: "Encontre serviços com o VeVolt Mercado",
        paragraphs: [
          "Além dos eletropostos, o VeVolt Mercado reúne lojas, concessionárias, oficinas, peças e locadoras. Use a localização ou o CEP para entender o que existe perto da parada e abra rota, contato ou avaliações externas quando disponíveis.",
        ],
      },
    ],
    faqs: [
      ["Recarga em shopping é sempre grátis?", "Não. Energia, estacionamento e taxas variam por operador e estabelecimento."],
      ["Posso deixar o carro após terminar?", "Siga as regras do local e libere a vaga assim que possível para não impedir o uso por outra pessoa."],
    ],
    sources: [sources.aneel, sources.maps],
  },
];

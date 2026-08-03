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
    image: "blog/ponto-recarga-perto.webp",
    imageAlt: "Motorista consulta o celular enquanto o carro elétrico recarrega em um eletroposto urbano",
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
    image: "blog/custo-recarga.webp",
    imageAlt: "Motorista compara no celular o consumo e o custo da recarga de um carro elétrico",
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
    image: "blog/tempo-recarga.webp",
    imageAlt: "Carro elétrico conectado a um carregador residencial ao lado de um relógio",
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
    image: "blog/tipos-conectores.webp",
    imageAlt: "Conectores Tipo 2, CCS2, CHAdeMO e GB/T organizados para comparação",
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
    image: "blog/planejar-viagem.webp",
    imageAlt: "Motorista planeja uma viagem de carro elétrico em uma parada de rodovia",
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
    title: "Carregador de carro elétrico em condomínio: gestão, agenda e rateio",
    description:
      "Guia completo para organizar carregadores, moradores, agenda, consumo manual, regras de uso e rateio de energia no condomínio.",
    keyword: "carregador carro elétrico condomínio",
    cluster: "condo",
    image: "blog/recarga-condominio.webp",
    imageAlt: "Moradores e administradora organizam a recarga de carros elétricos no condomínio",
    lead:
      "Um carregador de carro elétrico em condomínio exige duas frentes diferentes: a solução técnica, conduzida por profissionais habilitados, e a gestão cotidiana de moradores, horários, registros e critérios de rateio. O VeVolt Condo atua nessa segunda frente.",
    sections: [
      {
        heading: "Separe a instalação elétrica da gestão de uso",
        paragraphs: [
          "Antes de definir agenda ou rateio de energia, o condomínio precisa avaliar capacidade da instalação, demanda simultânea, proteções, trajeto dos cabos e exigências de segurança. Essa análise pertence a profissional habilitado e deve considerar distribuidora, normas técnicas, legislação e Corpo de Bombeiros da localidade.",
          "A gestão de recarga em condomínios começa depois que o cenário técnico está definido. É nessa etapa que síndico e administradora precisam organizar quem usa, quando usa, como registra a sessão e como os custos informados serão conferidos. Um aplicativo de gestão não substitui projeto, instalação, laudo, medidor ou sistema de controle elétrico.",
        ],
      },
      {
        heading: "Escolha entre uso individual e carregador compartilhado",
        paragraphs: [
          "Uma vaga com infraestrutura individual tende a concentrar responsabilidade e consumo em uma unidade. Já o carregador compartilhado em condomínio exige regras comuns, agenda e uma forma compreensível de registrar o uso. A decisão depende da configuração das vagas, da capacidade elétrica, do orçamento e das deliberações internas.",
          "Quando vários moradores usam o mesmo equipamento, deixar o processo apenas em mensagens costuma gerar dúvidas: quem reservou, qual horário termina, quanto foi informado de consumo e qual critério será usado no fechamento. Uma central única reduz esse ruído sem assumir o controle físico do carregador.",
        ],
      },
      {
        heading: "Crie regras de uso antes do primeiro agendamento",
        bullets: [
          "Quem pode usar cada carregador",
          "Duração máxima, tolerância e tratamento de atrasos",
          "Como registrar consumo, custo e ocorrências",
          "Critério de rateio de energia e data de fechamento",
          "Responsabilidade por cabo, vaga e liberação após uso",
        ],
        paragraphs: [
          "Regras simples e visíveis reduzem conflitos. A agenda do carregador deve refletir a capacidade operacional aprovada pelo condomínio, períodos de manutenção e eventuais restrições definidas pela solução técnica. O app organiza reservas; ele não calcula capacidade elétrica nem libera potência.",
        ],
      },
      {
        heading: "Organize moradores, agenda e sessões com o VeVolt Condo",
        paragraphs: [
          "O administrador cria uma central privada, cadastra até 10 carregadores e convida até 50 participantes. Moradores convidados entram sem Premium, consultam horários e fazem o agendamento do carregador sem conflito. Premium e Condo são assinaturas separadas.",
          "Após a recarga, o morador registra manualmente sessão, consumo e custo. Esses dados apoiam o controle de recarga e o fechamento mensal por unidade. O VeVolt não lê medidor, não valida a quantidade de energia e não transfere valores; a conferência continua com síndico ou administradora.",
        ],
      },
      {
        heading: "Faça o rateio com critérios verificáveis",
        paragraphs: [
          "O rateio de energia pode considerar consumo atribuído à sessão, tarifa adotada pelo condomínio e outros critérios aprovados internamente. O importante é que fonte do dado, período e fórmula sejam conhecidos pelos participantes. Se houver medição dedicada, seus valores devem ser lidos e validados pelo processo responsável, fora do VeVolt.",
          "Evite tratar o total digitado como medição automática. O app organiza os registros informados e facilita a revisão; não emite conta de energia nem substitui sistemas de faturamento.",
        ],
      },
      {
        heading: "Documente decisões e revise o processo",
        paragraphs: [
          "Mantenha projeto, responsabilidade técnica, regras aprovadas, manutenções e alterações em registros próprios do condomínio. Revise agenda, limites e critérios de rateio quando o número de veículos crescer. Uma governança simples ajuda a administração a responder aos moradores com consistência.",
          "Em São Paulo, a Lei estadual 18.403/2026 trata de instalações de recarga em condomínios e reforça compatibilidade elétrica, execução por profissional habilitado e comunicação à administração em situações abrangidas. Regras variam por estado, município e edificação; confirme sempre o contexto local.",
        ],
      },
      {
        heading: "Entenda os limites do aplicativo",
        paragraphs: [
          "O VeVolt Condo organiza participantes, carregadores cadastrados, agenda, sessões, consumo manual e rateio manual. Ele não instala equipamentos, não faz engenharia, não mede energia automaticamente, não cobra energia, não aciona o carregador, não executa OCPP e não realiza balanceamento de carga.",
          "Novos clientes elegíveis podem testar o plano Condo mensal grátis por 15 dias no Google Play. O plano anual continua disponível, mas não oferece um segundo teste.",
        ],
      },
    ],
    faqs: [
      ["O VeVolt instala ou controla o carregador?", "Não. O Condo organiza participantes, agenda, sessões, consumo manual e rateio. Projeto, instalação e controle físico são externos ao app."],
      ["Moradores precisam assinar o Premium?", "Não. O administrador assina o Condo e moradores convidados participam da central sem Premium."],
      ["O rateio é calculado com medição automática?", "Não. Consumo e custos são informados manualmente e devem ser conferidos pelo condomínio."],
      ["A mesma regra vale em todo o Brasil?", "Não necessariamente. Consulte legislação, distribuidora, normas técnicas e Corpo de Bombeiros da sua localidade."],
    ],
    contextLinks: [
      { href: "/condo", label: "Conheça a gestão de recarga do VeVolt Condo" },
      { href: "/blog/gestao-recarga-condominios", label: "Como organizar carregadores e moradores" },
      { href: "/blog/rateio-energia-carro-eletrico-condominio", label: "Como fazer o rateio de energia" },
      { href: "/blog/carregador-compartilhado-condominio", label: "Regras e agenda para carregador compartilhado" },
      { href: "/blog/aplicativo-agendar-carregador-condominio", label: "Aplicativo para agendar o carregador" },
    ],
    sources: [sources.spCondo, sources.cbmba, sources.aneel],
  },
  {
    slug: "recarga-rapida-carro-eletrico-boas-praticas",
    title: "Recarga rápida no carro elétrico: quando usar e o que observar",
    description:
      "Entenda potência, curva de carga, temperatura e boas práticas para usar carregadores rápidos.",
    keyword: "recarga rápida carro elétrico",
    image: "blog/recarga-rapida.webp",
    imageAlt: "Carro elétrico conectado a um carregador rápido de corrente contínua",
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
    image: "blog/recarga-gratis-ou-paga.webp",
    imageAlt: "Motorista verifica no celular as condições de cobrança antes da recarga pública",
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
    image: "blog/verificar-funcionamento.webp",
    imageAlt: "Motorista consulta no celular o funcionamento de um eletroposto antes de sair",
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
    image: "blog/sem-carregador-casa.webp",
    imageAlt: "Morador de apartamento recarrega o carro elétrico em um ponto público do bairro",
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
    image: "blog/eletrico-versus-gasolina.webp",
    imageAlt: "Comparação entre carro elétrico em recarga e carro a gasolina no posto",
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
    image: "blog/mapa-eletropostos-brasil.webp",
    imageAlt: "Mapa ilustrado do Brasil com pontos de recarga conectados por rotas",
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
    image: "blog/recarga-comercio-hotel.webp",
    imageAlt: "Carro elétrico recarrega em complexo com mercado, hotel e centro comercial",
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
  {
    slug: "gestao-recarga-condominios",
    title: "Gestão de recarga em condomínios: como organizar carregadores e moradores",
    description: "Veja como síndicos e administradoras podem organizar participantes, carregadores, agenda, sessões e rateio manual no condomínio.",
    keyword: "gestão de recarga em condomínios",
    cluster: "condo",
    image: "blog/recarga-condominio.webp",
    imageAlt: "Síndico organiza moradores, agenda e carregadores compartilhados do condomínio",
    lead: "A gestão de recarga em condomínios transforma decisões técnicas já aprovadas em uma rotina clara para administração e moradores. O objetivo é saber quem participa, quais carregadores existem, como os horários são organizados e como cada sessão entra no fechamento.",
    sections: [
      { heading: "Mapeie a operação antes de escolher a ferramenta", paragraphs: ["Liste carregadores disponíveis, vagas atendidas, moradores autorizados, horários possíveis e responsável por revisar os registros. Essa fotografia evita cadastrar uma disponibilidade maior do que a operação realmente suporta.", "Capacidade elétrica, proteção, instalação e segurança devem ter sido avaliadas por profissionais habilitados. O sistema de gestão organiza o uso aprovado; ele não define potência, dimensiona circuitos ou substitui documentação técnica."] },
      { heading: "Centralize moradores e carregadores", paragraphs: ["Uma central privada reduz informações espalhadas por mensagens e planilhas. No VeVolt Condo, o administrador cadastra até 10 carregadores e convida até 50 participantes. Moradores convidados participam sem Premium.", "Defina também quem pode reservar, qual carregador atende cada grupo e quem resolve ocorrências. Regras simples são mais fáceis de aplicar e revisar."] },
      { heading: "Conecte agenda, sessão e fechamento", bullets: ["Reserva com início e fim definidos", "Registro manual de consumo e custo", "Identificação da unidade responsável", "Revisão periódica pela administração", "Critério de rateio conhecido pelos participantes"], paragraphs: ["O valor da gestão está na continuidade entre as etapas. O horário reservado precisa corresponder a uma sessão e o registro dessa sessão precisa chegar ao fechamento mensal de forma compreensível."] },
      { heading: "Use o VeVolt Condo dentro dos limites corretos", paragraphs: ["O VeVolt organiza moradores, agenda, sessões, consumo e rateio manual. Não aciona carregadores, não mede energia automaticamente, não cobra energia, não executa OCPP e não faz balanceamento de carga.", "Novos clientes elegíveis podem testar o plano Condo mensal grátis por 15 dias no Google Play. Premium e Condo são assinaturas separadas."] },
    ],
    faqs: [["Quem administra a central?", "O assinante do VeVolt Condo cria a central, cadastra carregadores e convida os participantes."], ["O morador precisa pagar Premium?", "Não. Moradores convidados entram na central Condo sem Premium."], ["O app substitui a avaliação elétrica?", "Não. Projeto, capacidade, instalação e segurança continuam com profissionais e responsáveis externos ao app."]],
    contextLinks: [{ href: "/condo", label: "Conheça o VeVolt Condo" }, { href: "/blog/carregador-carro-eletrico-condominio", label: "Leia o guia completo sobre recarga em condomínio" }, { href: "/blog/aplicativo-agendar-carregador-condominio", label: "Veja como funciona o agendamento por aplicativo" }],
    sources: [sources.aneel, sources.spCondo, sources.cbmba],
  },
  {
    slug: "rateio-energia-carro-eletrico-condominio",
    title: "Como fazer o rateio de energia do carro elétrico no condomínio",
    description: "Entenda dados, critérios e conferências para dividir custos de recarga com registros manuais claros e verificáveis.",
    keyword: "rateio energia carro elétrico condomínio",
    cluster: "condo",
    image: "blog/custo-recarga.webp",
    imageAlt: "Síndico confere consumo e rateio manual das recargas de carros elétricos",
    lead: "Um rateio compreensível começa pela origem do consumo, pela tarifa adotada e por um período de fechamento bem definido. O aplicativo pode organizar os registros, mas a medição e a cobrança continuam sob responsabilidade do condomínio e de seus sistemas.",
    sections: [
      { heading: "Defina de onde vem o consumo", paragraphs: ["O condomínio precisa estabelecer qual leitura ou informação será usada para atribuir energia a cada sessão. Pode existir medição dedicada ou outro procedimento aprovado, mas o dado deve ser verificável e associado ao morador ou à unidade correta.", "No VeVolt Condo, consumo e custo são digitados manualmente. O app não consulta medidores e não transforma um valor informado em medição automática."] },
      { heading: "Escolha um critério transparente", bullets: ["Período de apuração e data de corte", "Quantidade de energia atribuída a cada sessão", "Tarifa ou fórmula aprovada", "Tratamento de perdas e custos adicionais", "Responsável pela conferência"], paragraphs: ["O critério precisa ser conhecido antes do fechamento. Mudanças de tarifa ou fórmula devem ser comunicadas e documentadas para que os participantes consigam reproduzir a conta."] },
      { heading: "Revise antes de lançar a cobrança", paragraphs: ["Confira sessões incompletas, valores duplicados, unidade selecionada e divergências com a fonte de medição adotada. O total organizado pelo app serve como apoio operacional, não como fatura de energia.", "O VeVolt não recebe o pagamento do morador nem cobra a energia. O lançamento condominial, recibo ou outro processo financeiro acontece fora do aplicativo."] },
      { heading: "Mantenha histórico suficiente para auditoria", paragraphs: ["Guarde documentos e leituras no sistema oficial do condomínio pelo prazo definido pela administração. No VeVolt, use o fechamento para reunir os registros manuais do período e facilitar a conferência entre participantes."] },
    ],
    faqs: [["O VeVolt calcula o consumo automaticamente?", "Não. Energia e custo são informados manualmente."], ["O app cobra o morador?", "Não. O VeVolt organiza registros; cobrança e recebimento ocorrem fora dele."], ["Qual tarifa deve ser usada?", "O critério deve ser definido e validado pelo condomínio conforme seu contexto, documentos e orientação profissional aplicável."]],
    contextLinks: [{ href: "/condo", label: "Organize o rateio manual com o VeVolt Condo" }, { href: "/blog/carregador-carro-eletrico-condominio", label: "Volte ao guia de carregador em condomínio" }, { href: "/blog/gestao-recarga-condominios", label: "Estruture a gestão de carregadores e moradores" }],
    sources: [sources.aneel, sources.inmetroConsumption],
  },
  {
    slug: "carregador-compartilhado-condominio",
    title: "Carregador compartilhado em condomínio: regras, agenda e controle de uso",
    description: "Organize reservas, atrasos, sessões e responsabilidades para reduzir conflitos no uso de um carregador compartilhado.",
    keyword: "carregador compartilhado condomínio",
    cluster: "condo",
    image: "blog/recarga-condominio.webp",
    imageAlt: "Moradores consultam regras e agenda de um carregador compartilhado no condomínio",
    lead: "Compartilhar um carregador funciona melhor quando todos conhecem as mesmas regras. Agenda, duração, tolerância, registro da sessão e liberação da vaga precisam formar um processo simples para moradores e administração.",
    sections: [
      { heading: "Defina quem pode reservar", paragraphs: ["Cadastre apenas participantes autorizados e deixe claro se visitantes ou veículos temporários podem usar a estrutura. No VeVolt Condo, a administração envia convites e mantém a central restrita aos membros aceitos.", "O acesso ao aplicativo não substitui chaves, cartões ou controles físicos do equipamento. Esses mecanismos permanecem separados."] },
      { heading: "Crie regras de agenda que caibam na rotina", bullets: ["Duração mínima e máxima", "Antecedência para reservar ou cancelar", "Tolerância para início e término", "Procedimento em caso de atraso", "Bloqueio de períodos de manutenção"], paragraphs: ["Evite janelas excessivamente longas e regras difíceis de fiscalizar. A agenda deve equilibrar o tempo necessário para recarregar com a oportunidade de uso pelos demais moradores."] },
      { heading: "Ligue cada reserva ao registro da sessão", paragraphs: ["Depois do uso, registre consumo e custo manualmente e informe ocorrências relevantes. Essa continuidade ajuda a administração a distinguir reserva cancelada, uso efetivo e sessão sem dados suficientes para o fechamento.", "O VeVolt não verifica se o cabo foi conectado nem encerra a recarga. A disciplina operacional continua com os participantes."] },
      { heading: "Revise conflitos e ajuste as regras", paragraphs: ["Acompanhe horários disputados, cancelamentos tardios e sessões que ultrapassam o período. Regras podem evoluir com a demanda, desde que as mudanças sejam comunicadas e aplicadas de maneira consistente."] },
    ],
    faqs: [["O app impede fisicamente o uso fora do horário?", "Não. A agenda organiza reservas, mas não bloqueia ou aciona o carregador."], ["Quantos carregadores podem ser cadastrados?", "Até 10 carregadores por central VeVolt Condo."], ["Como evitar reserva duplicada?", "Os moradores consultam a disponibilidade e agendam horários sem sobreposição dentro da central."]],
    contextLinks: [{ href: "/condo", label: "Gerencie a agenda com o VeVolt Condo" }, { href: "/blog/carregador-carro-eletrico-condominio", label: "Leia o artigo pilar sobre recarga em condomínio" }, { href: "/blog/rateio-energia-carro-eletrico-condominio", label: "Entenda o rateio das sessões" }],
    sources: [sources.spCondo, sources.cbmba],
  },
  {
    slug: "aplicativo-agendar-carregador-condominio",
    title: "Aplicativo para agendar carregador de carro elétrico no condomínio",
    description: "Veja como organizar moradores, horários sem conflito, sessões e consumo manual em uma central privada de recarga.",
    keyword: "aplicativo agendar carregador condomínio",
    cluster: "condo",
    image: "blog/recarga-condominio.webp",
    imageAlt: "Aplicativo VeVolt Condo mostra agenda de carregador para moradores",
    lead: "Um aplicativo para agendar carregador no condomínio substitui conversas dispersas por uma visão comum de participantes, equipamentos e horários. A ferramenta organiza o uso; o carregador e a instalação elétrica continuam independentes.",
    sections: [
      { heading: "O que procurar em um aplicativo de agenda", bullets: ["Central privada para o condomínio", "Convites para moradores", "Cadastro dos carregadores", "Horários sem sobreposição", "Registro de sessões e consumo", "Fechamento para rateio manual"], paragraphs: ["A agenda precisa estar conectada à realidade da administração. Não basta mostrar um calendário: é importante saber quem reservou, qual carregador será usado e quais dados deverão ser informados após a sessão."] },
      { heading: "Como funciona no VeVolt Condo", paragraphs: ["O administrador assina o VeVolt Condo, cria a central, cadastra até 10 carregadores e convida até 50 participantes. Moradores convidados não precisam de Premium para participar.", "Cada morador consulta a agenda e escolhe um horário disponível. Depois da recarga, registra manualmente consumo e custo para apoiar o fechamento mensal."] },
      { heading: "O que o aplicativo não faz", paragraphs: ["O VeVolt não se conecta ao carregador por OCPP, não inicia ou encerra sessões, não mede energia automaticamente e não faz balanceamento de carga. Também não cobra a energia dos moradores.", "Esses limites evitam confundir organização administrativa com controle elétrico. Instalação, segurança, medição e cobrança permanecem nos processos responsáveis do condomínio."] },
      { heading: "Teste o fluxo antes de ampliar", paragraphs: ["Comece com regras claras, cadastre os equipamentos aprovados e acompanhe a experiência dos participantes. Novos clientes elegíveis podem testar o plano Condo mensal grátis por 15 dias no Google Play; o preço posterior é o valor local exibido pela loja."] },
    ],
    faqs: [["O VeVolt funciona como controle remoto do carregador?", "Não. Ele organiza agenda e registros, sem acionar o equipamento."], ["Moradores convidados pagam assinatura?", "Não. A assinatura Condo é do administrador; convidados participam sem Premium."], ["Existe teste grátis?", "Novos clientes elegíveis podem testar o plano Condo mensal por 15 dias no Google Play."]],
    contextLinks: [{ href: "/condo", label: "Teste a gestão do VeVolt Condo" }, { href: "/blog/carregador-carro-eletrico-condominio", label: "Leia o guia completo para condomínios" }, { href: "/blog/carregador-compartilhado-condominio", label: "Defina regras para o carregador compartilhado" }],
    sources: [sources.aneel, sources.spCondo],
  },
];

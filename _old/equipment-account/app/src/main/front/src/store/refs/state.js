export default {
  eqTypes: [],
  eqStates: [],
  persons: [],
  selectedGuids: [],
  actions: [
    { id: 1, label: 'Создание', disabled: true },
    { id: 2, label: 'Изменение', disabled: true },
    { id: 3, label: 'Ремонт' },
    { id: 4, label: 'Списание' },
  ].filter(e => !e.disabled),
}

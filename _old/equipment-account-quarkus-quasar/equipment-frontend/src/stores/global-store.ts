import { defineStore } from 'pinia';
import { Reference } from 'src/components/models';

interface StoreState {
  selectedGuids: string[];
  types: Reference[];
  states: Reference[];
  persons: Reference[];
}

export const useGlobalStore = defineStore('globalStore', {
  state: () => {
    return {
      selectedGuids: [],
      states: [
        { id: 1, name: 'Новый' },
        { id: 2, name: 'В эксплуатации' },
        { id: 3, name: 'В резерве' },
        { id: 4, name: 'В ремонте' },
        { id: 5, name: 'Списан' },
        { id: 6, name: 'state0.6415836732604231' },
        { id: 7, name: 'state 0.30081789597396824' },
        { id: 8, name: 'ttt' },
        { id: 9, name: 'yyy' },
      ],
      types: [
        { id: 7, name: 'Notebook' },
        { id: 9, name: 'type 0.13426956922414446' },
        { id: 8, name: 'type0.5021845515835421' },
        { id: 3, name: 'МФУ' },
        { id: 2, name: 'Ноутбук' },
        { id: 1, name: 'Системный блок' },
      ],
      persons: [
        { id: 1, name: 'Коско Александр Николаевич' },
        { id: 2, name: 'Бурая Наталья Владимировна' },
        { id: 3, name: 'Лукашевич Тимофей Викентьевич' },
        { id: 4, name: 'person0.9312200988162168' },
        { id: 5, name: 'person0.8335635554693495' },
      ],
    } as StoreState;
  },
  getters: {
    selectedGuidsCount: (state) => state.selectedGuids.length,
  },
  actions: {
    selectGuids(guids: string[]) {
      const m = this.selectedGuids;
      for (const guid of guids) {
        if (m.indexOf(guid) === -1) m.push(guid);
      }
    },
    unSelectGuids(guids: string[]) {
      const m = this.selectedGuids;
      for (const guid of guids) {
        const i = m.indexOf(guid);
        if (i !== -1) m.splice(i, 1);
      }
    },
    clearSelectedGuids() {
      this.selectedGuids = [];
    },
  },
});

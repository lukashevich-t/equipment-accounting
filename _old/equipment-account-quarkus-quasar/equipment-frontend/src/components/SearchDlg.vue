<template>
  <div>
    <q-card>
      <q-card-section class="row items-center">
        <div class="text-h6">Критерии поиска</div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup />
      </q-card-section>

      <q-card-section class="items-center">
        <q-input
          outlined
          v-model="crit.invNumber"
          label="Инвентарник"
          dense
          @keyup.enter="search"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="crit.invNumber = ''"
              class="cursor-pointer"
              v-if="criteria.invNumber"
            />
          </template>
        </q-input>

        <q-input
          outlined
          v-model="crit.serial"
          label="Серийник"
          dense
          @keyup.enter="search"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="crit.serial = ''"
              class="cursor-pointer"
              v-if="criteria.serial"
            />
          </template>
        </q-input>

        <q-input
          outlined
          v-model="crit.comment"
          label="Комментарий"
          dense
          @keyup.enter="search"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="crit.comment = ''"
              class="cursor-pointer"
              v-if="criteria.comment"
            />
          </template>
        </q-input>

        <!-- <q-input
          outlined
          v-model="crit.displayDate"
          label="Дата покупки"
          dense
          @keyup.enter="search"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="crit.displayDate = ''"
              class="cursor-pointer"
              v-if="criteria.displayDate"
            />
            <q-icon name="event" class="cursor-pointer">
              <q-popup-proxy
                ref="qDateProxy"
                transition-show="scale"
                transition-hide="scale"
              >
                <q-date
                  v-model="crit.displayDate"
                  @input="() => qDateProxy.hide()"
                  mask="DD.MM.YYYY"
                  :locale="locale"
                />
              </q-popup-proxy>
            </q-icon>
          </template>
        </q-input> -->

        <q-input
          outlined
          v-model="crit.purchaseDate"
          label="Дата покупки"
          dense
          @keyup.enter="search"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="crit.purchaseDate = ''"
              class="cursor-pointer"
              v-if="criteria.purchaseDate"
            />
            <q-icon name="event" class="cursor-pointer">
              <q-popup-proxy
                cover
                transition-show="scale"
                transition-hide="scale"
              >
                <q-date
                  v-model="crit.purchaseDate"
                  mask="DD.MM.YYYY"
                  :locale="locale"
                />
              </q-popup-proxy>
            </q-icon>
          </template>
        </q-input>

        <q-select
          outlined
          dense
          v-model="crit.typeId"
          hide-selected
          fill-input
          :options="typesFiltered"
          emit-value
          map-options
          use-input
          @filter="filterTypes"
          label="Тип"
          option-value="id"
          option-label="name"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="crit.typeId = 0"
              class="cursor-pointer"
              v-if="criteria.typeId"
            />
          </template>
        </q-select>

        <!-- <q-select
          outlined
          dense
          v-model="crit.stateId"
          hide-selected
          fill-input
          :options="statesFiltered"
          emit-value
          map-options
          use-input
          @filter="filterStates"
          label="Состояние"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="crit.stateId = 0"
              class="cursor-pointer"
              v-if="criteria.state_id"
            />
          </template>
        </q-select>

        <q-select
          outlined
          dense
          v-model="crit.personId"
          hide-selected
          fill-input
          :options="personsFiltered"
          emit-value
          map-options
          use-input
          @filter="filterPersons"
          label="Ответственный"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="crit.personId = 0"
              class="cursor-pointer"
              v-if="criteria.person_id"
            />
          </template>
        </q-select>-->
      </q-card-section>

      <q-card-actions align="right">
        <!-- <q-btn flat label="test" color="primary" @click="$log.debug(criteria)"/> -->
        <q-btn flat label="Отмена" color="primary" v-close-popup />
        <q-btn flat label="Очистить" @click="clear" />
        <q-btn
          icon="search"
          color="primary"
          @click="search"
          :disable="!containsCriteria || v$.$invalid"
        />
      </q-card-actions>
    </q-card>
    {{ crit }}
  </div>
</template>

<script lang="ts">
import {
  defineComponent,
  PropType,
  computed,
  ref,
  toRef,
  // Ref,
} from 'vue';
import { numeric } from '@vuelidate/validators';
import { Criteria } from 'components/models';
import useVuelidate from '@vuelidate/core';
import { locale } from 'src/lib/consts';
import { useGlobalStore } from 'src/stores/global-store';

function useTypes() {
  const store = useGlobalStore();
  const typesFiltered = ref(store.types);
  function filterTypes(val: string, update): void {
    if (val === '') {
      update(() => {
        typesFiltered.value = store.types;
        // here you have access to "ref" which
        // is the Vue reference of the QSelect
      });
      return;
    }

    update(() => {
      const needle = val.toLowerCase();
      typesFiltered.value = store.types.filter(
        (v) => v.name.toLowerCase().indexOf(needle) > -1
      );
    });
  }
  return { filterTypes, typesFiltered };
}

function useReference(name: string) {
  debugger;
  const store = useGlobalStore();
  console.log(store[name]);
 
  return {  };
}

export default defineComponent({
  name: 'SearchDlg',
  props: {
    criteria: {
      type: Object as PropType<Criteria>,
      required: true,
    },
  },
  setup(props, context) {
    let crit = toRef(props, 'criteria');

    function clear() {
      crit.value.invNumber = '';
      crit.value.serial = '';
      crit.value.displayDate = null;
      crit.value.purchaseDate = null;
      crit.value.personId = 0;
      crit.value.stateId = 0;
      crit.value.typeId = 0;
      crit.value.comment = '';
    }
    const containsCriteria = computed(() => {
      for (let k in crit.value) {
        if (crit.value[k]) {
          return true;
        }
      }
      return false;
    });
    const validations = {
      personId: { numeric },
      stateId: { numeric },
      typeId: { numeric },
      purchaseDate: {},
    };
    function search() {
      console.log('12');
    }
    // return { ...useClickCount(), ...useDisplayTodo(toRef(props, 'todos')) };
    return {
      ...useTypes(),
      ...useReference('states'),
      locale,
      crit,
      clear,
      search,
      containsCriteria,
      v$: useVuelidate(validations, crit),
    };
  },
});
</script>

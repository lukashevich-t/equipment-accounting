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
          v-model="criteria.inv_number"
          label="Инвентарник"
          dense
          @keyup.enter="search"
          ref="invNumberInput"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="criteria.inv_number = ''"
              class="cursor-pointer"
              v-if="criteria.inv_number"
            />
          </template>
        </q-input>

        <q-input
          outlined
          v-model="criteria.serial"
          label="Серийник"
          dense
          @keyup.enter="search"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="criteria.serial = ''"
              class="cursor-pointer"
              v-if="criteria.serial"
            />
          </template>
        </q-input>

        <q-input
          outlined
          v-model="criteria.comment"
          label="Комментарий"
          dense
          @keyup.enter="search"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="criteria.comment = ''"
              class="cursor-pointer"
              v-if="criteria.comment"
            />
          </template>
        </q-input>

        <q-input
          outlined
          v-model="criteria.displayDate"
          label="Дата покупки"
          dense
          @keyup.enter="search"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="criteria.displayDate = ''"
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
                  v-model="criteria.displayDate"
                  @input="() => $refs.qDateProxy.hide()"
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
          v-model="criteria.type_id"
          hide-selected
          fill-input
          :options="typesFiltered"
          emit-value
          map-options
          use-input
          @filter="filterTypes"
          label="Тип"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="criteria.type_id = 0"
              class="cursor-pointer"
              v-if="criteria.type_id"
            />
          </template>
        </q-select>

        <q-select
          outlined
          dense
          v-model="criteria.state_id"
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
              @click="criteria.state_id = 0"
              class="cursor-pointer"
              v-if="criteria.state_id"
            />
          </template>
        </q-select>

        <q-select
          outlined
          dense
          v-model="criteria.person_id"
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
              @click="criteria.person_id = 0"
              class="cursor-pointer"
              v-if="criteria.person_id"
            />
          </template>
        </q-select>
      </q-card-section>

      <q-card-actions align="right">
        <!-- <q-btn flat label="test" color="primary" @click="$log.debug(criteria)"/> -->
        <q-btn flat label="Отмена" color="primary" v-close-popup />
        <q-btn flat label="Очистить" @click="clear" />
        <q-btn
          icon="search"
          color="primary"
          @click="search"
          :disable="!containsCriteria || $v.criteria.$invalid"
        />
      </q-card-actions>
    </q-card>
  </div>
</template>

<script>
import { locale } from 'src/lib/consts'
import { regex } from 'src/lib/validators'
import { mapGetters } from 'vuex'
import { filterQSelect } from 'src/lib/funcs'
export default {
  data() {
    return {
      typesFiltered: [],
      statesFiltered: [],
      personsFiltered: [],
      locale,
    }
  },
  validations() {
    return {
      criteria: {
        displayDate: { regex: regex(/^\d{2}\.\d{2}\.\d{4}$/) },
      },
    }
  },
  methods: {
    search() {
      let vm = this
      if (!vm.containsCriteria || vm.$v.criteria.$invalid) return
      this.$emit('search', vm.criteria)
    },
    clear() {
      let c = this.criteria
      c.inv_number = ''
      c.serial = ''
      c.displayDate = null
      c.purchase_date = null
      c.person_id = 0
      c.state_id = 0
      c.type_id = 0
      c.comment = ''
    },
    filterTypes(val, update, abort) {
      filterQSelect(this, val, update, this.eqTypes, 'typesFiltered')
    },
    filterStates(val, update, abort) {
      filterQSelect(this, val, update, this.eqStates, 'statesFiltered')
    },
    filterPersons(val, update, abort) {
      filterQSelect(this, val, update, this.persons, 'personsFiltered')
    },
  },
  props: ['criteria'],
  computed: {
    ...mapGetters(['eqTypes', 'eqStates', 'persons']),
    containsCriteria() {
      let crit = this.criteria
      for (let k in crit) {
        if (crit[k]) {
          return true
        }
      }
      return false
    },
  },
  mounted() {
    let vm = this
    vm.typesFiltered = vm.eqTypes
    vm.statesFiltered = vm.eqStatеs
    vm.personsFiltered = vm.persons
    vm.$refs.invNumberInput.focus()
  },
}
</script>

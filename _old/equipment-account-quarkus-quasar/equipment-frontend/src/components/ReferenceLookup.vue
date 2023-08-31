<template>
  <q-select
    outlined
    dense
    :value="modelValue"
    @input="emitUpdate"
    hide-selected
    fill-input
    :options="filteredOptions"
    emit-value
    map-options
    use-input
    @filter="filterOptions"
    label="Тип"
    option-value="id"
    option-label="name"
  >
    <template v-slot:append>
      <q-icon
        name="close"
        @click="emitUpdate(0)"
        class="cursor-pointer"
        v-if="modelValue"
      />
    </template>
  </q-select>
</template>

<script lang="ts">
import { defineComponent, PropType /*, computed, ref */ } from 'vue';
import { Reference } from './models';
function useFilters() {
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

export default defineComponent({
  name: 'ReferenceLookup',
  emits: ['update:modelValue'],
  props: {
    modelValue: {
      type: Object as PropType<number>,
      required: true,
    },
    options: {
      type: Object as PropType<Array<Reference>>,
      required: true,
    },
    // criteria: {
    //   type: Object as PropType<Criteria>,
    //   required: true,
    // },
  },
  setup(props, context) {
    function emitUpdate(x: any) {
      console.log(x);
    }
    return { emitUpdate };
  },
});
</script>

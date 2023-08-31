<template>
  <q-btn @click="increment"></q-btn>
</template>

<script lang="ts">
import {
  defineComponent,
  PropType /*, computed, ref, reactive, toRefs, PropType */,
} from 'vue';
export default defineComponent({
  name: 'SimpleComponent',
  props: {
    modelValue: {
      type: Object as PropType<number>,
      required: true,
    },
  },
  setup(/* props, context */) {

    return {
      increment: function() { modelValue }
    };
  },
});
</script>

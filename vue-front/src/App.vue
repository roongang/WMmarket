<template>
  <WMHeader></WMHeader>
  <WMContent></WMContent>
  <WMFooter></WMFooter>
</template>

<script>
import WMHeader from "@/components/WMHeader";
import WMContent from "@/components/WMContent";
import WMFooter from "@/components/WMFooter";

export default {
  name: 'App',
  components: {
    WMHeader,
    WMContent,
    WMFooter
  }
}
</script>

<style>
</style>

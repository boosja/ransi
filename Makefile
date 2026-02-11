.PHONY: perf-bb perf-clj

perf-bb:
	bb --classpath dev -x ransi.print-perf/print-perf

perf-clj:
	clj -X:dev ransi.print-perf/print-perf

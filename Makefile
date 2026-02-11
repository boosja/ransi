.PHONY: perf-bb perf-clj

perf-bb:
	bb --classpath dev -x terminator.print-perf/print-perf

perf-clj:
	clj -X:dev terminator.print-perf/print-perf

# Terminator

A data-driven rendering library for Babashka that renders hiccup to the
terminal. Highly inspired by [Replicant](https://github.com/cjohansen/replicant).

```clj
(terminator.core/render
  [[:fg/blue "Dreams float through "
    [:bg/cyan "the cosmic soup "
     [:fx/bold "with THUNDER "
      [:fx/underline "and lightning "]
      "pure power, "]
     "gentle again, "]
    "back to dreams, "]
   "then reality strikes. "
   [:bg/green "Pizza parties "
    [:fg/yellow "with disco balls "
     [:fx/italic "spinning madly! "
      [:bg/red "Alarm bells, "
       [:fg/white "panic mode, "]
       "back to dancing, "]
      "back to pizza, "]
     "no spinning, "]
    "no disco, "]
   "no pizza."])
```

![result](/examples/dreams.webp)

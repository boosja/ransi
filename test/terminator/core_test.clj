(ns terminator.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [terminator.core :as terminator]))

(deftest find-antonym-test
  (testing "Returns the antonym for bold"
    (is (= (terminator/find-antonym [:fx/bold])
           :fx/normal)))

  (testing "When foreground color, returns previous foreground color"
    (is (= (terminator/find-antonym [:fg/magenta :bg/red :fg/green :fx/bold :fx/underline
                                :fg/blue])
           :fg/green)))

  (testing "When background color, returns previous background color"
    (is (= (terminator/find-antonym [:bg/magenta :fg/red :fx/bold :bg/green :fx/underline
                                :bg/blue])
           :bg/green))))

(deftest render-test
  (testing "Renders text"
    (is (= (terminator/render "This is blue!")
           "This is blue!")))

  (testing "Renders blue text"
    (is (= (terminator/render [:fg/blue "This is blue!"])
           "[34mThis is blue![39m")))

  (testing "Renders nested bg color"
    (is (= (terminator/render [:fg/blue [:bg/green "This is blue!"]])
           "[34m[42mThis is blue![49m[39m")))

  (testing "Add newline with :newline"
    (is (= (terminator/render [:fg/blue "This is blue!" [:newline]])
           "[34mThis is blue!\n[39m")))

  (testing "Add newline with :br"
    (is (= (terminator/render [:fg/blue "This is blue!" [:br]])
           "[34mThis is blue!\n[39m")))

  (testing "Within a vector"
    (is (= (terminator/render [[:fg/blue "This is blue!"]
                          [:bg/green "Wow!"]])
           "[34mThis is blue![39m[42mWow![49m")))

  (testing "Within a list"
    (is (= (terminator/render (list [:fg/blue "This is blue!"]
                               [:bg/green "Wow!"]))
           "[34mThis is blue![39m[42mWow![49m")))

  (testing "Remembers parent color and reinstates it"
    (is (= (terminator/render [:bg/magenta "I'm magenta "
                          [:fg/black "and black "
                           [:bg/red "Other bg, "
                            [:fg/white "other fg, "]
                            "back to black fg, "]
                           "back to magenta bg"]])
           (str/join
            ["[45mI'm magenta "
             "[30mand black "
             "[41mOther bg, "
             "[37mother fg, "
             "[30mback to black fg, "
             "[45mback to magenta bg"
             "[39m"
             "[49m"]))))

  (testing "with multiple deep branches"
    (is (= (terminator/render [[:fg/blue "This is blue "
                           [:bg/cyan "and this with cyan bg "
                            [:fx/bold "and BOLD "
                             [:fx/underline "and underline "]
                             "no underline, "]
                            "no bold, "]
                           "no bg, "]
                          "and no nothing. "
                          [:bg/green "Now I'm green "
                           [:fg/yellow "and yellow "
                            [:fx/italic "and wonky! "
                             [:bg/red "Other bg, "
                              [:fg/white "other fg, "]
                              "back to yellow fg, "]
                             "back to green bg, "]
                            "no wonks, "]
                           "no yellow, "]
                          "no green."])
           (str/join
            ["[34mThis is blue "
             "[46mand this with cyan bg "
             "[1mand BOLD "
             "[4mand underline "
             "[24mno underline, "
             "[22mno bold, "
             "[49mno bg, "
             "[39mand no nothing. "
             "[42mNow I'm green "
             "[33mand yellow "
             "[3mand wonky! "
             "[41mOther bg, "
             "[37mother fg, "
             "[33mback to yellow fg, "
             "[42mback to green bg, "
             "[23mno wonks, "
             "[39mno yellow, "
             "[49mno green."])))))

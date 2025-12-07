# 🎄 Advent of Code 🎄

This repository contains my (Bart Oostveen) Advent of Code solutions. This year (2025, at least as of writing), I am not
going to race to be the fastest to complete the puzzles. Instead, I'll submit my solutions later on the day. This is
because
of [Eric Wastl's decision to not have a global leaderboard this year](https://adventofcode.com/2025/about#faq_leaderboard).

## Runner

This repository features a very simple runner that is totally not coded in 10 minutes late at night before AoC started.
It downloads your inputs from the Advent of Code website, then runs the solution.
In order to run it, make sure you either have Nix, or Gradle and a JDK installed.

### Run solutions

Make sure there is an AoC token inside the `AOC_TOKEN` environment variable. After that, you can run the runner (
`RunnerKt`) with the following arguments: `<year> <day>`. For example: `2025 5`, for day 5 of 2025. By default, it'll
run the current day's solution.

**Using Nix:**

```shell
$ nix develop
# or either
$ direnv allow
```

Then, in all cases, run:

```shell
$ gradle run --args="<year> <day>"
# or use ./gradlew if you prefer that
```

## Progress

### 2025

- [x] Day 01
- [x] Day 02
- [x] Day 03
- [x] Day 04
- [x] Day 05
- [x] Day 06
- [x] Day 07
- [ ] Day 08
- [ ] Day 09
- [ ] Day 10
- [ ] Day 11
- [ ] Day 12

## Misc

### Formatting

```shell
$ nix fmt
```

## License

This repository contains code that is fully released to the public domain using the Unlicense. You can find a
copy [here](LICENSE).

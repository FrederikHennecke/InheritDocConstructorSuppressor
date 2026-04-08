# PHP InheritDoc Constructor Suppressor

Small PhpStorm plugin for a specific false positive around `@inheritDoc` on child constructors.

In some cases PhpStorm treats the child constructor like it only has its own parameter list and ignores the parent docblock details well enough to understand variadic children. The result is warnings like:

```text
Expected parameter of type 'array', 'string' provided
```

This plugin suppresses that warning when the constructor being called is documented with `@inheritDoc` or `{@inheritDoc}`.

## Example

```php
class MMLbase {
	/**
	 * @param MMLbase|string|null ...$children
	 */
	public function __construct(
		string $name,
		string $texclass = '',
		array $attributes = [],
		...$children
	) {}
}

class MMLmrow extends MMLbase {
	/** @inheritDoc */
	public function __construct(
		string $texclass = 'ORD',
		array $attributes = [],
		...$children
	) {
		parent::__construct('mrow', $texclass, $attributes, ...$children);
	}
}

new MMLmrow('', [], 'child');
```

Without the plugin, PhpStorm may flag the last line as if the third argument still belonged to `$attributes`.

## What It Does

The suppression is intentionally narrow:

- it only targets constructor-call parameter type warnings
- it only applies inside `new ...(...)` expressions
- it only activates when the resolved constructor has `@inheritDoc`

It uses both an inspection suppressor and a highlight filter because PhpStorm does not surface this warning through one path only.

## Build

Use a full JDK 21, not a JRE.

```bash
./gradlew buildPlugin
```

The plugin ZIP is written to `build/distributions/`.

## Install

In PhpStorm, open `Settings > Plugins > Gear icon > Install Plugin from Disk...`, pick the ZIP from `build/distributions/`, then restart the IDE.

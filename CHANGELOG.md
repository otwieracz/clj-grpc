# Change Log

## 0.1.2 - 2019-11-14
### Changed
- `system` definition is now a function, not a macro (which caused problems in some cases)
- `req` parameter in `defrpc` is now convenient map with easily-accessible kebab-cased properties of request.
- No need to provide `services` vector anymore when building `GrpcServer` instance.

## 0.1.0 - 2019-11-12
### Added
- Initial release


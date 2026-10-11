import { useState, useMemo } from "react";
import {
  HiCheck,
  HiMagnifyingGlass,
  HiOutlineSparkles,
} from "react-icons/hi2";

export default function InterestPicker({
  tags = [],
  selectedIds = [],
  onToggle,
  disabled = false,
  compact = false,
}) {
  const [searchQuery, setSearchQuery] = useState("");

  const filteredTags = useMemo(() => {
    if (!searchQuery.trim()) {
      return tags;
    }
    const query = searchQuery.toLowerCase().trim();
    return tags.filter(
      (tag) =>
        tag.name.toLowerCase().includes(query) ||
        (tag.description &&
          tag.description.toLowerCase().includes(query))
    );
  }, [tags, searchQuery]);

  const selectedCount = selectedIds.length;

  return (
    <div className="space-y-4">
      {/* Search and Selection Status Bar */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="relative flex-1">
          <HiMagnifyingGlass
            className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
            size={18}
          />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search interests (e.g. Nature, Waterfalls, Culture)..."
            disabled={disabled}
            className="
              w-full
              rounded-xl
              border
              border-slate-200
              bg-white
              py-2.5
              pl-10
              pr-4
              text-sm
              text-slate-800
              placeholder-slate-400
              outline-none
              transition
              focus:border-[var(--color-primary)]
              focus:ring-2
              focus:ring-emerald-100
              disabled:bg-slate-50
            "
          />
        </div>

        <div className="flex items-center justify-between gap-3 text-xs sm:justify-end">
          <span className="font-medium text-slate-600">
            <span className="font-bold text-[var(--color-primary)]">
              {selectedCount}
            </span>{" "}
            {selectedCount === 1 ? "interest" : "interests"} selected
          </span>

          {selectedCount > 0 && !disabled && (
            <button
              type="button"
              onClick={() => {
                tags.forEach((tag) => {
                  if (selectedIds.includes(tag.id)) {
                    onToggle(tag.id);
                  }
                });
              }}
              className="text-xs font-semibold text-slate-500 hover:text-slate-800 hover:underline"
            >
              Clear all
            </button>
          )}
        </div>
      </div>

      {/* Tags Grid - Pinterest style tiles */}
      {filteredTags.length === 0 ? (
        <div className="rounded-xl border border-dashed border-slate-200 py-8 text-center text-sm text-slate-500">
          No interests match "{searchQuery}"
        </div>
      ) : (
        <div
          className={`
            grid gap-3
            ${
              compact
                ? "grid-cols-1 sm:grid-cols-2 md:grid-cols-3"
                : "grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
            }
          `}
        >
          {filteredTags.map((tag) => {
            const isSelected = selectedIds.includes(tag.id);

            return (
              <div
                key={tag.id}
                role="button"
                tabIndex={disabled ? -1 : 0}
                aria-pressed={isSelected}
                onClick={() => {
                  if (!disabled) {
                    onToggle(tag.id);
                  }
                }}
                onKeyDown={(e) => {
                  if (
                    !disabled &&
                    (e.key === "Enter" || e.key === " ")
                  ) {
                    e.preventDefault();
                    onToggle(tag.id);
                  }
                }}
                className={`
                  group
                  relative
                  flex
                  cursor-pointer
                  flex-col
                  justify-between
                  rounded-2xl
                  border
                  p-4
                  text-left
                  transition-all
                  duration-200
                  ${
                    disabled
                      ? "cursor-not-allowed opacity-60"
                      : "hover:-translate-y-0.5 hover:shadow-md"
                  }
                  ${
                    isSelected
                      ? "border-emerald-500 bg-emerald-50/80 shadow-sm ring-2 ring-emerald-500/20"
                      : "border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50/60"
                  }
                `}
              >
                <div>
                  <div className="flex items-start justify-between gap-2">
                    <div className="flex items-center gap-2">
                      <span
                        className={`
                          flex
                          h-7
                          w-7
                          items-center
                          justify-center
                          rounded-lg
                          text-xs
                          font-semibold
                          ${
                            isSelected
                              ? "bg-emerald-200 text-emerald-800"
                              : "bg-slate-100 text-slate-600 group-hover:bg-slate-200"
                          }
                        `}
                      >
                        <HiOutlineSparkles size={14} />
                      </span>
                      <h4
                        className={`
                          font-semibold
                          ${
                            isSelected
                              ? "text-emerald-950"
                              : "text-slate-800"
                          }
                        `}
                      >
                        {tag.name}
                      </h4>
                    </div>

                    {/* Checkbox indicator */}
                    <div
                      className={`
                        flex
                        h-5
                        w-5
                        shrink-0
                        items-center
                        justify-center
                        rounded-full
                        border
                        transition-all
                        ${
                          isSelected
                            ? "border-emerald-600 bg-emerald-600 text-white"
                            : "border-slate-300 bg-white group-hover:border-slate-400"
                        }
                      `}
                    >
                      {isSelected && (
                        <HiCheck size={13} strokeWidth={2.5} />
                      )}
                    </div>
                  </div>

                  {tag.description && (
                    <p
                      className={`
                        mt-2.5
                        text-xs
                        leading-relaxed
                        line-clamp-2
                        ${
                          isSelected
                            ? "text-emerald-900/80"
                            : "text-slate-500"
                        }
                      `}
                    >
                      {tag.description}
                    </p>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

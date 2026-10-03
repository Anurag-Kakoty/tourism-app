export default function AdminTable({
  columns = [],
  data = [],
  rowKey = "id",
  emptyMessage = "No records available.",
  renderActions,
}) {
  return (
    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      {data.length === 0 ? (
        <div className="px-6 py-12 text-center">
          <p className="text-slate-500">
            {emptyMessage}
          </p>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-slate-200 bg-slate-50">
                {columns.map((column) => (
                  <th
                    key={column.key}
                    className={`
                      px-6
                      py-4
                      text-left
                      text-sm
                      font-semibold
                      text-slate-700
                      ${column.headerClassName || ""}
                    `}
                  >
                    {column.label}
                  </th>
                ))}

                {renderActions && (
                  <th className="px-6 py-4 text-right text-sm font-semibold text-slate-700">
                    Actions
                  </th>
                )}
              </tr>
            </thead>

            <tbody>
              {data.map((item) => (
                <tr
                  key={item[rowKey]}
                  className="border-b border-slate-100 last:border-0"
                >
                  {columns.map((column) => (
                    <td
                      key={column.key}
                      className={`
                        px-6
                        py-4
                        text-sm
                        text-slate-600
                        ${column.cellClassName || ""}
                      `}
                    >
                      {column.render
                        ? column.render(item)
                        : item[column.key]}
                    </td>
                  ))}

                  {renderActions && (
                    <td className="px-6 py-4">
                      <div className="flex justify-end gap-2">
                        {renderActions(item)}
                      </div>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
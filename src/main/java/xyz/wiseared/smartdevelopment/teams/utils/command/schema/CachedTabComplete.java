package xyz.wiseared.smartdevelopment.teams.utils.command.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.wiseared.smartdevelopment.warsimulator.utils.command.adapters.ParameterAdapter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CachedTabComplete {

    final ParameterAdapter<?> parameterAdapter;

    final List<String> constant;
}

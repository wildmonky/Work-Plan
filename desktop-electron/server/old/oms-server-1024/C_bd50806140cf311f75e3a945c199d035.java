import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_bd50806140cf311f75e3a945c199d035
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return StringUtils.isEmpty(mapRow.get("DDZY"))?"":"<span datavalue='"+mapRow.get("DDZY")+"' onclick='pw_notify_deadnumline_view(this);'>查看</span>";

    }
}

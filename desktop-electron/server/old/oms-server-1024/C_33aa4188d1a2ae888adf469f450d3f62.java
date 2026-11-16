import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_33aa4188d1a2ae888adf469f450d3f62
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return mapRow.get("BZ").toString().isEmpty()?"":"<span datavalue='"+mapRow.get("BZ")+"' onclick='pw_notify_deadnumline_view(this);'>查看</span>";

    }
}

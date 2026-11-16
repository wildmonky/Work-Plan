import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_28f80f5e737d6f9325b3fc36824fc192
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return mapRow.get("DDZY").toString().isEmpty()?"":"<span datavalue='"+mapRow.get("DDZY")+"' onclick='pw_notify_deadnumline_view(this);'>查看</span>";

    }
}
